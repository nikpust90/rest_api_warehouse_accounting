package rest_api_warehouse_accounting.service;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest_api_warehouse_accounting.model.directory.Product;
import rest_api_warehouse_accounting.model.document.IncomingDocument;
import rest_api_warehouse_accounting.model.document.item.IncomingItem;
import rest_api_warehouse_accounting.repositories.IncomingDocumentRepository;
import rest_api_warehouse_accounting.repositories.IncomingItemRepository;
import rest_api_warehouse_accounting.repositories.ProductRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomingService {
    private final IncomingDocumentRepository repository;
    private final ProductRepository productRepository;
    private final IncomingItemRepository itemRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден: " + id));

        product.setName(productDetails.getName());
        product.setQuantity(productDetails.getQuantity());
        product.setSku(productDetails.getSku());
        product.setCategory(productDetails.getCategory());

        return productRepository.save(product);
    }

    /**
     * Получение всех документов поступления.
     *
     * @return список документов поступления
     */
    public List<IncomingDocument> getAllIncomingDocuments() {
        return repository.findAll();
    }

    /**
     * Создание нового документа поступления с обновлением остатков товаров и созданием IncomingItem.
     *
     * @param document документ поступления
     * @return сохраненный документ поступления
     * @throws IllegalArgumentException если документ с таким номером и датой уже существует
     */
    @Transactional
    public IncomingDocument createIncomingDocument(@Valid IncomingDocument document) {
        // Проверка на уникальность документа по номеру и дате
        if (isDocumentExists(document.getDocumentNumber(), document.getCreatedAt())) {
            throw new IllegalArgumentException("Документ с таким номером и датой уже существует.");
        }

        // Проверка наличия товаров в документе
        if (document.getItems() == null || document.getItems().isEmpty()) {
            throw new IllegalArgumentException("Нельзя создать документ без товаров!");
        }

        // Сохранение документа
        IncomingDocument savedDocument = repository.save(document);

        // Получаем все ID продуктов, чтобы избежать множества SQL-запросов
        List<Long> productIds = document.getItems().stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toList());

        // Загружаем все продукты за один запрос
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // Обновляем товары и создаем позиции поступления
        List<IncomingItem> incomingItems = new ArrayList<>();
        for (IncomingItem item : document.getItems()) {
            Product product = productMap.get(item.getProduct().getId());
            if (product == null) {
                throw new IllegalArgumentException("Продукт не найден: " + item.getProduct().getId());
            }

            // Увеличиваем количество товара
            product.setQuantity(product.getQuantity() + item.getQuantity());

            // Создаем IncomingItem
            item.setIncomingDocument(savedDocument);
            incomingItems.add(item);
        }

        // Сохраняем все обновленные товары и позиции поступления за один запрос
        productRepository.saveAll(productMap.values());
        itemRepository.saveAll(incomingItems);

        // Hibernate автоматически обновит `savedDocument.items`, повторный save не нужен
        return savedDocument;
    }

    /**
     * Проверяет, существует ли документ с таким номером и датой.
     *
     * @param documentNumber номер документа
     * @param documentDate   дата документа
     * @return true, если документ уже существует, иначе false
     */
    public boolean isDocumentExists(String documentNumber, LocalDateTime documentDate) {
        return repository.findByDocumentNumberAndCreatedAt(documentNumber, documentDate).isPresent();
    }
}
