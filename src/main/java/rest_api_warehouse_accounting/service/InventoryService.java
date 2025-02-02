package rest_api_warehouse_accounting.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest_api_warehouse_accounting.model.directory.Product;
import rest_api_warehouse_accounting.model.document.InventoryDocument;
import rest_api_warehouse_accounting.model.document.item.InventoryItem;
import rest_api_warehouse_accounting.repositories.InventoryItemRepository;
import rest_api_warehouse_accounting.repositories.InventoryDocumentRepository;
import rest_api_warehouse_accounting.repositories.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryDocumentRepository inventoryDocumentRepository;

    private final InventoryItemRepository inventoryItemRepository;

    private final ProductRepository productRepository;

    /**
     * Получение всех документов инвентаризации.
     *
     * @return список документов инвентаризации
     */
    public List<InventoryDocument> getAllInventoryDocuments() {
        return inventoryDocumentRepository.findAll();

    }

    /**
     * Создание нового документа инвентаризации с обновлением остатков товаров и созданием InventoryItem.
     *
     * @param document документ инвентаризации
     * @return сохраненный документ инвентаризации
     * @throws IllegalArgumentException если документ с таким номером и датой уже существует
     */
    @Transactional
    public InventoryDocument createInventoryDocument(@Valid InventoryDocument document) {



        // Проверка на уникальность документа по номеру и дате
        if (isDocumentExists(document.getDocumentNumber(), document.getCreatedAt())) {
            throw new IllegalArgumentException("Документ с таким номером и датой уже существует.");
        }

        // Проверка наличия товаров в документе
        if (document.getItems() == null || document.getItems().isEmpty()) {
            throw new IllegalArgumentException("Нельзя создать документ без товаров!");
        }

        InventoryDocument saveDocument = inventoryDocumentRepository.save(document);

        List<InventoryItem> inventoryItems = new ArrayList<>();
        for (InventoryItem item: document.getItems()) {

            item.setInventoryDocument(saveDocument); // привязал к item ссылку на документ
            inventoryItems.add(item); //добавил item в список

        }

        inventoryItemRepository.saveAll(inventoryItems);


        return saveDocument;
    }

    /**
     * Проверяет, существует ли документ с таким номером и датой.
     *
     * @param documentNumber номер документа
     * @param documentDate   дата документа
     * @return true, если документ уже существует, иначе false
     */
    public boolean isDocumentExists(String documentNumber, LocalDateTime documentDate) {
        return inventoryDocumentRepository.findByDocumentNumberAndCreatedAt(documentNumber, documentDate).isPresent();
    }

    /**
     * Заполняет документ инвентаризации позициями товаров,
     * у которых количество (остаток) больше нуля.
     *
     * @param document документ инвентаризации, который нужно заполнить
     * @return заполненный документ, сохранённый в БД
     */
    @Transactional
    public InventoryDocument fillDocumentWithItems(InventoryDocument document) {

        // Проверка на уникальность документа по номеру и дате
        if (isDocumentExists(document.getDocumentNumber(), document.getCreatedAt())) {
            throw new IllegalArgumentException("Документ с таким номером и датой уже существует.");
        }

        // Получаем список товаров, у которых количество больше нуля
        List<Product> availableProducts = productRepository.findByQuantityGreaterThan(0);

        // Заполняем документ позициями товаров
        List<InventoryItem> listItems = new ArrayList<>();
        for(Product product: availableProducts) {
            InventoryItem item = new InventoryItem();
            item.setProduct(product);
            item.setInventoryDocument(document);
            item.setQuantityPlan(product.getQuantity());
            item.setQuantityFact(0);
            item.setDeviation(product.getQuantity());

            listItems.add(item);

        }

        // Устанавливаем список позиций в документе
        document.setItems(listItems);

        // Сохраняем документ инвентаризации вместе с позициями.
        // Если в аннотации @OneToMany указан cascade = CascadeType.ALL, то можно сохранить только документ,
        // а позиции сохранятся автоматически. В противном случае можно отдельно сохранить позиции.
        InventoryDocument savedDocument = inventoryDocumentRepository.save(document);

        // Если необходимо отдельно сохранить позиции, можно раскомментировать следующую строку:
        // inventoryItemRepository.saveAll(items);

        return savedDocument;

    }

    /**
     * Обновляет существующий документ инвентаризации.
     *
     * 1. Проверяет, существует ли документ в базе по его ID.
     * 2. Если документ не найден, выбрасывает исключение.
     * 3. Обновляет поля документа, включая список товаров (InventoryItem).
     * 4. Если в обновленном документе есть новые товары, они добавляются.
     * 5. Если какие-то товары удалены, они удаляются из базы.
     * 6. Сохраняет обновленный документ в базе и возвращает его.
     *
     * @param document Объект InventoryDocument с новыми данными.
     * @return Обновленный объект InventoryDocument.
     * @throws IllegalArgumentException если документ не найден.
     */
    @Transactional
    public InventoryDocument updateInventoryDocument(@Valid InventoryDocument document) {
        // 1. Получаем существующий документ из базы по id
        List<InventoryDocument> documents = inventoryDocumentRepository.findAllById(Collections.singleton(document.getId()));

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("Документ не найден.");
        }

        InventoryDocument existingDocument = documents.get(0);

        // 2. Обновляем основные поля документа (при необходимости)
        existingDocument.setDocumentNumber(document.getDocumentNumber());
        existingDocument.setCreatedAt(document.getCreatedAt());

        // 3. Обновление списка позиций (итемов)
        // Создаём карту существующих итемов по их id для быстрого поиска
        Map<Long, InventoryItem> existingItemsMap = existingDocument.getItems().stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(InventoryItem::getId, item -> item));

        // Итоговый список позиций, который будет установлен в документе
        List<InventoryItem> mergedItems  = new ArrayList<>();

        // Проходим по итемам, присланным в обновлённом документе
        for (InventoryItem updatedItem : document.getItems()) {
            if (updatedItem.getId() != null) {
                // Если итем уже существует, то обновляем его
                InventoryItem existingItem = existingItemsMap.get(updatedItem.getId());
                if (existingItem != null) {
                    // Обновляем поля существующего итема

                    existingItem.setQuantityPlan(updatedItem.getQuantityPlan());
                    existingItem.setQuantityFact(updatedItem.getQuantityFact());
                    existingItem.setDeviation(updatedItem.getDeviation());
                    // При необходимости можно обновлять и связанные объекты (например, продукт)
                    existingItem.setProduct(updatedItem.getProduct());

                    mergedItems.add(existingItem);
                    // Удаляем обработанный итем из карты, чтобы оставить только те, которые нужно удалить
                    existingItemsMap.remove(existingItem.getId());
                }else {
                    // Если пришёл итем с id, которого нет в существующем документе,
                    // можно либо выбросить исключение, либо добавить как новый.
                    // Здесь рассматриваем его как новый:
                    updatedItem.setInventoryDocument(existingDocument);
                    mergedItems.add(updatedItem);
                }
            } else {
                // Если итем новый (id == null), устанавливаем связь с документом и добавляем в список
                updatedItem.setInventoryDocument(existingDocument);
                mergedItems.add(updatedItem);
            }
        }

        // 4. Оставшиеся в карте существующие итемы – были удалены на фронтенде.
        // Если используется orphanRemoval, достаточно обновить список итемов в документе.
        // Иначе можно явно удалить их из базы:
//        for (InventoryItem removedItem : existingItemsMap.values()) {
//            inventoryItemRepository.delete(removedItem);
//        }


        // 5. Устанавливаем объединённый список итемов в документ
        existingDocument.setItems(mergedItems);

        // 6. Сохраняем обновлённый документ, изменения распространятся и на связанные итемы
        InventoryDocument savedDocument = inventoryDocumentRepository.save(existingDocument);

        return savedDocument;

    }
}
