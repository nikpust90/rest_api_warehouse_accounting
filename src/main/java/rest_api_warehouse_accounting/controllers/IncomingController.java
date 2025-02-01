package rest_api_warehouse_accounting.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest_api_warehouse_accounting.model.Product;
import rest_api_warehouse_accounting.model.document.IncomingDocument;
import rest_api_warehouse_accounting.service.IncomingService;

import java.util.List;

// Аннотация @RestController указывает, что данный класс является REST-контроллером
// и все его методы будут возвращать JSON-ответы.
@RestController

// @RequestMapping указывает базовый URL для всех эндпоинтов этого контроллера.
// Все запросы, начинающиеся с "/api/incoming", будут обрабатываться в этом классе.
@RequestMapping("/api/incoming")
public class IncomingController {

    // Автоматически внедряем сервис IncomingService, который отвечает за бизнес-логику
    @Autowired
    private IncomingService incomingService;

    /**
     * Создание нового товара.
     *
     * @param product - объект Product, который приходит в теле запроса.
     * @return ResponseEntity с созданным товаром или сообщением об ошибке.
     */
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
        try {
            Product createdProduct = incomingService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении товара");
        }
    }

    /**
     * Обновление существующего товара.
     *
     * @param id - ID товара, который нужно обновить.
     * @param productDetails - новые данные для товара.
     * @return Обновленный товар или сообщение об ошибке.
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = incomingService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении товара");
        }
    }


    /**
     * Получение списка всех документов поступления товаров.
     *
     * @return ResponseEntity со списком документов или сообщением об ошибке.
     */
    @GetMapping("/documents") // Обработчик GET-запросов по пути "/api/incoming/documents"
    public ResponseEntity<?> getIncomingDocuments() {
        try {
            // Запрашиваем список всех документов поступления из сервиса
            return ResponseEntity.ok(incomingService.getAllIncomingDocuments());
        } catch (Exception e) {
            // В случае ошибки возвращаем статус 500 (внутренняя ошибка сервера)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении документов");
        }
    }

    /**
     * Создание нового документа поступления товаров.
     *
     * @param document - объект IncomingDocument, который приходит в теле запроса.
     * @return ResponseEntity с созданным документом или сообщением об ошибке.
     */
    @PostMapping("/documents") // Обработчик POST-запросов по пути "/api/incoming/documents"
    public ResponseEntity<?> createIncomingDocument(
            @Valid @RequestBody IncomingDocument document) { // @Valid - валидация входного объекта
        try {
            // Передаем документ в сервис для обработки и сохранения в БД
            IncomingDocument createdDocument = incomingService.createIncomingDocument(document);
            // Возвращаем статус 201 (Created) и сам созданный документ
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
        } catch (IllegalArgumentException e) {
            // Если возникла ошибка из-за некорректных данных (например, дублирование),
            // возвращаем статус 400 (Bad Request) и сообщение ошибки
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // В случае неожиданной ошибки возвращаем статус 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении документа");
        }
    }
}
