package rest_api_warehouse_accounting.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest_api_warehouse_accounting.dto.document.OutgoingDocumentDto;
import rest_api_warehouse_accounting.dto.document.item.OutgoingItemDto;
import rest_api_warehouse_accounting.mappers.OutgoingDocumentMapper;
import rest_api_warehouse_accounting.model.document.OutgoingDocument;
import rest_api_warehouse_accounting.model.document.item.OutgoingItem;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Product;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;
import rest_api_warehouse_accounting.model.regisrt.Stock;
import rest_api_warehouse_accounting.repositories.StockRepository;
import rest_api_warehouse_accounting.repositories.document.OutgoingDocumentRepository;
import rest_api_warehouse_accounting.repositories.document.item.OutgoingItemRepository;
import rest_api_warehouse_accounting.repositories.referenceBooks.BinRepository;
import rest_api_warehouse_accounting.repositories.referenceBooks.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OutgoingService {

    private final OutgoingDocumentRepository repository;
    private final ProductRepository productRepository;
    private final BinRepository binRepository;
    private final StockRepository stockRepository;
    private final OutgoingItemRepository itemRepository;
    private final OutgoingDocumentMapper mapper;

    @Transactional
    public List<OutgoingDocumentDto> getAllOutgoingDocuments() {
        log.info("Получение всех документов списания");
        List<OutgoingDocument> documents = repository.findAll();
        return mapper.toDTOList(documents);
    }

    @Transactional(readOnly = true)
    public OutgoingDocumentDto getOutgoingDocumentById(Long id) {
        log.info("Получение документа списания по ID: {}", id);
        OutgoingDocument document = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Документ списания с ID " + id + " не найден"));
        return mapper.toDTO(document);
    }

    /**
     * Создает новый документ списания и обновляет складские остатки.
     *
     * @param documentDto DTO документа списания.
     * @return Созданный документ списания в формате DTO.
     */
    @Transactional
    public OutgoingDocumentDto createOutgoingDocument(@Valid OutgoingDocumentDto documentDto) {
        log.info("Создание документа списания: {}", documentDto.getDocumentNumber());

        validateDocument(documentDto);

        // Создаем объект документа, но не сохраняем сразу
        OutgoingDocument document = saveOutgoingDocument(documentDto);

        // Загружаем продукты и ячейки со стеллажами
        Map<Long, Product> productMap = loadProducts(documentDto);
        Map<String, Bin> binMap = loadBinsWithShelves(documentDto);

        // Привязываем итемы к документу
        List<OutgoingItem> outgoingItems = processOutgoingItems(documentDto, document, productMap, binMap);
        outgoingItems.forEach(item -> item.setOutgoingDocument(document));

        document.setItems(outgoingItems); // Добавляем итемы к документу

        // Обновляем остатки на складе
        List<Stock> updatedStocks = updateStockLevels(documentDto, productMap, binMap, document);

        // Сохраняем документ с итемами
        OutgoingDocument savedDocument = repository.save(document);

        // Сохраняем товары, итемы и остатки
        saveAllEntities(productMap, outgoingItems, updatedStocks);

        log.info("Документ списания {} успешно создан", savedDocument.getDocumentNumber());
        return mapper.toDTO(savedDocument);
    }

    /**
     * Проверяет уникальность документа и наличие товаров.
     *
     * @param documentDto DTO документа списания.
     */
    @Transactional
    protected void validateDocument(OutgoingDocumentDto documentDto) {
        if (repository.existsByDocumentNumberAndCreatedAt(documentDto.getDocumentNumber(), documentDto.getCreatedAt())) {
            throw new IllegalArgumentException("Документ с таким номером и датой уже существует.");
        }

        if (documentDto.getItems() == null || documentDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Нельзя создать документ без товаров!");
        }
    }

    /**
     * Сохраняет новый документ списания.
     *
     * @param documentDto DTO документа списания.
     * @return Сохраненный документ списания.
     */
    @Transactional
    protected OutgoingDocument saveOutgoingDocument(OutgoingDocumentDto documentDto) {
        OutgoingDocument document = mapper.toEntity(documentDto);
        return document;
    }

    /**
     * Загружает список продуктов по их ID.
     *
     * @param documentDto DTO документа списания.
     * @return Карта продуктов (ключ - ID продукта).
     */
    @Transactional
    protected Map<Long, Product> loadProducts(OutgoingDocumentDto documentDto) {
        List<Long> productIds = documentDto.getItems().stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toList());

        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    /**
     * Загружает список ячеек склада по их ID.
     *
     * @param documentDto DTO документа списания.
     * @return Карта ячеек склада (ключ - binId).
     */
    @Transactional
    protected Map<String, Bin> loadBinsWithShelves(OutgoingDocumentDto documentDto) {
        List<String> binIds = documentDto.getItems().stream()
                .map(item -> item.getBin().getBinId())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return binRepository.findAllByBinIdIn(binIds).stream()
                .collect(Collectors.toMap(Bin::getBinId, Function.identity()));
    }

    /**
     * Обрабатывает позиции списания и формирует список {@link OutgoingItem}.
     *
     * @param documentDto DTO документа списания.
     * @param savedDocument Сохраненный документ списания.
     * @param productMap Карта загруженных продуктов.
     * @param binMap Карта загруженных ячеек склада.
     * @return Список позиций списания.
     */
    @Transactional
    protected List<OutgoingItem> processOutgoingItems(
            OutgoingDocumentDto documentDto,
            OutgoingDocument savedDocument,
            Map<Long, Product> productMap,
            Map<String, Bin> binMap
    ) {
        return documentDto.getItems().stream()
                .map(itemDto -> {
                    Product product = productMap.get(itemDto.getProduct().getId());
                    Bin bin = binMap.get(itemDto.getBin().getBinId());

                    if (product == null) {
                        throw new IllegalArgumentException("Продукт не найден: " + itemDto.getProduct().getId());
                    }
                    if (bin == null) {
                        throw new IllegalArgumentException("Ячейка не найдена: " + itemDto.getBin().getBinId());
                    }

                    // Получаем стеллаж из ячейки
                    Shelf shelf = bin.getShelf();
                    if (shelf == null) {
                        throw new IllegalArgumentException("Стеллаж не найден для ячейки: " + bin.getBinId());
                    }

                    // Уменьшаем количество товара
                    if (product.getQuantity() < itemDto.getQuantity()) {
                        throw new IllegalArgumentException("Недостаточно товара на складе: " + product.getName());
                    }
                    product.setQuantity(product.getQuantity() - itemDto.getQuantity());

                    // Создаем OutgoingItem, привязываем к документу и стеллажу
                    OutgoingItem item = mapper.toEntity(itemDto, product, bin, shelf);
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Обновляет складские остатки по товарам и ячейкам на основе документа списания.
     *
     * @param documentDto DTO документа списания.
     * @param productMap Карта загруженных продуктов.
     * @param binMap Карта загруженных ячеек склада.
     * @param outgoingDocument Документ списания.
     * @return Список обновленных записей остатков {@link Stock}.
     */
    @Transactional
    protected List<Stock> updateStockLevels(
            OutgoingDocumentDto documentDto,
            Map<Long, Product> productMap,
            Map<String, Bin> binMap,
            OutgoingDocument outgoingDocument
    ) {
        List<Long> productIds = documentDto.getItems().stream()
                .map(item -> item.getProduct().getId())
                .distinct()
                .collect(Collectors.toList());

        List<String> binIds = documentDto.getItems().stream()
                .map(item -> item.getBin().getBinId())
                .distinct()
                .collect(Collectors.toList());

        // Загружаем существующие остатки с учетом документа списания
        Map<String, Stock> stockMap = stockRepository.findAllByProductIdsAndBinIdsAndOutgoingDocument(productIds, binIds, outgoingDocument.getId())
                .stream()
                .collect(Collectors.toMap(
                        stock -> stock.getProduct().getId() + "_" + stock.getBin().getBinId() + "_" + stock.getOutgoingDocument().getId(),
                        Function.identity()
                ));

        List<Stock> updatedStocks = new ArrayList<>();

        for (OutgoingItemDto itemDto : documentDto.getItems()) {
            Product product = productMap.get(itemDto.getProduct().getId());
            Bin bin = binMap.get(itemDto.getBin().getBinId());

            // Формируем новый ключ с учетом документа списания
            String stockKey = product.getId() + "_" + bin.getBinId() + "_" + outgoingDocument.getId();

            // Проверяем, есть ли уже остаток с таким productId, binId и outgoingDocumentId
            Stock stock = stockMap.get(stockKey);
            if (stock == null) {
                stock = new Stock(
                        null, product, bin, bin.getShelf(), bin.getShelf().getWarehouse(),
                        -itemDto.getQuantity(), null, outgoingDocument
                );
            } else {
                stock.setQuantity(stock.getQuantity() - itemDto.getQuantity());
            }

            updatedStocks.add(stock);
        }

        return updatedStocks;
    }

    /**
     * Сохраняет обновленные сущности в БД.
     *
     * @param productMap Карта обновленных продуктов.
     * @param outgoingItems Список позиций списания.
     * @param updatedStocks Список обновленных складских остатков.
     */
    private void saveAllEntities(
            Map<Long, Product> productMap,
            List<OutgoingItem> outgoingItems,
            List<Stock> updatedStocks
    ) {
        productRepository.saveAll(productMap.values());
        itemRepository.saveAll(outgoingItems);
        stockRepository.saveAll(updatedStocks);
    }
}