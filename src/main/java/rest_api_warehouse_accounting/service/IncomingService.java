package rest_api_warehouse_accounting.service;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest_api_warehouse_accounting.dto.document.IncomingDocumentDto;
import rest_api_warehouse_accounting.dto.document.item.IncomingItemDto;
import rest_api_warehouse_accounting.mappers.IncomingDocumentMapper;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Product;
import rest_api_warehouse_accounting.model.document.IncomingDocument;
import rest_api_warehouse_accounting.model.document.item.IncomingItem;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;
import rest_api_warehouse_accounting.model.regisrt.Stock;
import rest_api_warehouse_accounting.repositories.document.IncomingDocumentRepository;
import rest_api_warehouse_accounting.repositories.document.item.IncomingItemRepository;
import rest_api_warehouse_accounting.repositories.referenceBooks.BinRepository;
import rest_api_warehouse_accounting.repositories.referenceBooks.ProductRepository;
import rest_api_warehouse_accounting.repositories.StockRepository;


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
public class IncomingService {

    private final IncomingDocumentRepository repository;
    private final ProductRepository productRepository;
    private final BinRepository binRepository;
    private final StockRepository stockRepository;
    private final IncomingItemRepository itemRepository;
    private final IncomingDocumentMapper mapper;

    @Transactional
    public  List<IncomingDocumentDto> getAllIncomingDocuments() {
        log.info("Получение всех документов инвентаризации");
        List<IncomingDocument> documents =  repository.findAll();
        return mapper.toDTOList(documents);
    }

    /**
     * Создает новый документ поступления и обновляет складские остатки.
     *
     * @param documentDto DTO документа поступления.
     * @return Созданный документ поступления в формате DTO.
     */

    @Transactional
    public IncomingDocumentDto createIncomingDocument(@Valid IncomingDocumentDto documentDto) {
        log.info("Создание документа поступления: {}", documentDto.getDocumentNumber());

        validateDocument(documentDto);

        // Создаем объект документа, но не сохраняем сразу
        IncomingDocument document = saveIncomingDocument(documentDto);

        // Загружаем продукты и ячейки со стеллажами
        Map<Long, Product> productMap = loadProducts(documentDto);
        Map<String, Bin> binMap = loadBinsWithShelves(documentDto); // Теперь загружает стеллажи

        // Привязываем итемы к документу
        List<IncomingItem> incomingItems = processIncomingItems(documentDto, document, productMap, binMap);
        incomingItems.forEach(item -> item.setIncomingDocument(document)); // ✅ Присваивание документа выполняется 1 раз

        document.setItems(incomingItems);// Добавляем итемы к документу

        // Обновляем остатки на складе
        List<Stock> updatedStocks = updateStockLevels(documentDto, productMap, binMap, document);

        // Сохраняем документ с итемами
        IncomingDocument savedDocument = repository.save(document);

        // Сохраняем товары, итемы и остатки
        saveAllEntities(productMap, incomingItems, updatedStocks);

        log.info("Документ поступления {} успешно создан", savedDocument.getDocumentNumber());
        return mapper.toDTO(savedDocument);
    }

    /**
     * Проверяет уникальность документа и наличие товаров.
     *
     * @param documentDto DTO документа поступления.
     */
    @Transactional
    protected void validateDocument(IncomingDocumentDto documentDto) {
        if (repository.existsByDocumentNumberAndCreatedAt(documentDto.getDocumentNumber(), documentDto.getCreatedAt())) {
            throw new IllegalArgumentException("Документ с таким номером и датой уже существует.");
        }

        if (documentDto.getItems() == null || documentDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Нельзя создать документ без товаров!");
        }
    }

    /**
     * Сохраняет новый документ поступления.
     *
     * @param documentDto DTO документа поступления.
     * @return Сохраненный документ поступления.
     */
    @Transactional
    protected IncomingDocument saveIncomingDocument(IncomingDocumentDto documentDto) {
        IncomingDocument document = mapper.toEntity(documentDto);
        //return repository.save(document);
        return document;
    }

    /**
     * Загружает список продуктов по их ID.
     *
     * @param documentDto DTO документа поступления.
     * @return Карта продуктов (ключ - ID продукта).
     */
    @Transactional
    protected Map<Long, Product> loadProducts(IncomingDocumentDto documentDto) {
        List<Long> productIds = documentDto.getItems().stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toList());

        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    /**
     * Загружает список ячеек склада по их ID.
     *
     * @param documentDto DTO документа поступления.
     * @return Карта ячеек склада (ключ - binId).
     */
    @Transactional
    protected Map<String, Bin> loadBinsWithShelves(IncomingDocumentDto documentDto) {
        List<String> binIds = documentDto.getItems().stream()
                .map(item -> item.getBin().getBinId())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return binRepository.findAllByBinIdIn(binIds).stream()
                .collect(Collectors.toMap(Bin::getBinId, Function.identity()));
    }

    /**
     * Обрабатывает позиции поступления и формирует список {@link IncomingItem}.
     *
     * @param documentDto DTO документа поступления.
     * @param savedDocument Сохраненный документ поступления.
     * @param productMap Карта загруженных продуктов.
     * @param binMap Карта загруженных ячеек склада.
     * @return Список позиций поступления.
     */
    @Transactional
    protected List<IncomingItem> processIncomingItems(
            IncomingDocumentDto documentDto,
            IncomingDocument savedDocument,
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

                    // Увеличиваем количество товара
                    product.setQuantity(product.getQuantity() + itemDto.getQuantity());

                    // Создаем IncomingItem, привязываем к документу и стеллажу
                    IncomingItem item = mapper.toEntity(itemDto, product, bin, shelf);
//                    item.setShelf(shelf);
//                    item.setIncomingDocument(savedDocument);

                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Обновляет складские остатки по товарам и ячейкам на основе документа поступления.
     * Метод проверяет, существуют ли записи остатков (Stock) в базе, и обновляет их.
     * Если запись не найдена, создается новая.
     *
     * @param documentDto DTO документа поступления, содержащий информацию о товарах и их размещении.
     * @param productMap Карта загруженных продуктов, где ключ - ID продукта, а значение - объект {@link Product}.
     * @param binMap Карта загруженных ячеек склада, где ключ - ID ячейки, а значение - объект {@link Bin}.
     * @return Список обновленных записей остатков {@link Stock}.
     */
    @Transactional
    protected List<Stock> updateStockLevels(
            IncomingDocumentDto documentDto,
            Map<Long, Product> productMap,
            Map<String, Bin> binMap,
            IncomingDocument incomingDocument // ✅ Теперь передаем сам документ
    ) {
        List<Long> productIds = documentDto.getItems().stream()
                .map(item -> item.getProduct().getId())
                .distinct()
                .collect(Collectors.toList());

        List<String> binIds = documentDto.getItems().stream()
                .map(item -> item.getBin().getBinId())
                .distinct()
                .collect(Collectors.toList());

        // Загружаем существующие остатки с учетом документа поступления
        Map<String, Stock> stockMap = stockRepository.findAllByProductIdsAndBinIdsAndIncomingDocument(productIds, binIds, incomingDocument.getId())
                .stream()
                .collect(Collectors.toMap(
                        stock -> stock.getProduct().getId() + "_" + stock.getBin().getBinId() + "_" + stock.getIncomingDocument().getId(), // Ключ с учетом документа поступления
                        Function.identity()
                ));

        List<Stock> updatedStocks = new ArrayList<>();

        for (IncomingItemDto itemDto : documentDto.getItems()) {
            Product product = productMap.get(itemDto.getProduct().getId());
            Bin bin = binMap.get(itemDto.getBin().getBinId());

            // Формируем новый ключ с учетом документа поступления
            String stockKey = product.getId() + "_" + bin.getBinId() + "_" + incomingDocument.getId();

            // Проверяем, есть ли уже остаток с таким productId, binId и incomingDocumentId
            Stock stock = stockMap.get(stockKey);
            if (stock == null) {
                stock = new Stock(
                        null, product, bin, bin.getShelf(), bin.getShelf().getWarehouse(),
                        itemDto.getQuantity(), incomingDocument, null
                );
            }



            updatedStocks.add(stock);
        }

        return updatedStocks;
    }

    /**
     * Сохраняет обновленные сущности в БД.
     *
     * @param productMap Карта обновленных продуктов.
     * @param incomingItems Список позиций поступления.
     * @param updatedStocks Список обновленных складских остатков.
     */

    private void saveAllEntities(
            Map<Long, Product> productMap,
            List<IncomingItem> incomingItems,
            List<Stock> updatedStocks
    ) {
        productRepository.saveAll(productMap.values());
        itemRepository.saveAll(incomingItems);
        stockRepository.saveAll(updatedStocks);
    }


}
