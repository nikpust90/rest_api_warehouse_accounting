package rest_api_warehouse_accounting.service;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest_api_warehouse_accounting.model.directory.Product;
import rest_api_warehouse_accounting.model.document.RealizationDocument;
import rest_api_warehouse_accounting.model.document.item.RealizationItem;
import rest_api_warehouse_accounting.repositories.ProductRepository;
import rest_api_warehouse_accounting.repositories.RealizationDocumentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RealizationService {

    @Autowired
    private RealizationDocumentRepository realizationDocumentRepository;

    @Autowired
    private ProductRepository productRepository;



    /**
     * Создает документ реализации и обновляет количество товара на складе.
     *
     * @param document Объект RealizationDocument, содержащий позиции реализации
     * @return Созданный документ реализации
     * @throws IllegalArgumentException если товара недостаточно для реализации
     */
    @Transactional
    public RealizationDocument createRealizationDocument(@Valid RealizationDocument document) {
        // Проверяем наличие достаточного количества товара для реализации
        for (RealizationItem item : document.getItems()) {
            Long productId = item.getProduct().getId();
            int requestedQuantity = item.getQuantity();

            if (!isEnoughProductAvailable(productId, requestedQuantity)) {
                throw new IllegalArgumentException("Недостаточно товара для реализации (Товар ID: " + productId + ").");
            }
        }

        // Списываем товары с остатка
        for (RealizationItem item : document.getItems()) {
            Product product = item.getProduct();
            int newQuantity = product.getQuantity() - item.getQuantity();
            product.setQuantity(newQuantity); // Обновляем остаток товара
            productRepository.save(product); // Сохраняем изменения в БД
        }

        // Сохраняем документ реализации
        return realizationDocumentRepository.save(document);
    }

    /**
     * Проверяет, достаточно ли товара на складе для реализации.
     *
     * @param productId ID товара
     * @param quantity  Количество, которое требуется реализовать
     * @return true, если товара достаточно, иначе false
     */
    public boolean isEnoughProductAvailable(Long productId, int quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getQuantity() >= quantity) // Проверяем, есть ли нужное количество
                .orElse(false); // Если товар не найден, считаем, что его нет
    }

    /**
     * Получает список всех документов реализации.
     *
     * @return Список документов реализации
     */
    public List<RealizationDocument> getAllRealizationDocuments() {
        return realizationDocumentRepository.findAll();
    }
}
