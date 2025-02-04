package rest_api_warehouse_accounting.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest_api_warehouse_accounting.dto.InventoryDocumentDTO;
import rest_api_warehouse_accounting.model.directory.Product;
import rest_api_warehouse_accounting.model.document.IncomingDocument;
import rest_api_warehouse_accounting.model.document.InventoryDocument;
import rest_api_warehouse_accounting.service.IncomingService;
import rest_api_warehouse_accounting.service.InventoryService;

import java.util.List;

// Аннотация @RestController указывает, что данный класс является REST-контроллером
// и все его методы будут возвращать JSON-ответы.
@RestController

// @RequestMapping указывает базовый URL для всех эндпоинтов этого контроллера.
// Все запросы, начинающиеся с "/api/inventory", будут обрабатываться в этом классе.
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/documents") // Обработчик GET-запросов по пути "/api/inventory/documents"
    public ResponseEntity<?> getInventoryDocuments() {
        try {
            // Запрашиваем список всех документов инвентаризации из сервиса

            List<InventoryDocumentDTO> documents = inventoryService.getAllInventoryDocuments();
            return ResponseEntity.ok(documents);
            //return ResponseEntity.ok(inventoryService.getAllInventoryDocuments());
        } catch (Exception e) {
            // В случае ошибки возвращаем статус 500 (внутренняя ошибка сервера)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении документов");
        }
    }

    /**
     * Создание нового документа инвентаризации товаров.
     *
     * @param document - объект InventoryDocument, который приходит в теле запроса.
     * @return ResponseEntity с созданным документом или сообщением об ошибке.
     */
    @PostMapping("/documents") // Обработчик POST-запросов по пути "/api/inventory/documents"
    public ResponseEntity<?> createInventoryDocument(
            @Valid @RequestBody InventoryDocument document) { // @Valid - валидация входного объекта
        try {

            if (document.getId() == null) {
                // Если id равен null, то документ новый и выполняем операцию создания
                InventoryDocument createdDocument =  inventoryService.createInventoryDocument(document);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
            } else {
                // Если id присутствует, то документ существует и выполняем операцию обновления
                InventoryDocument savedDocument = inventoryService.updateInventoryDocument(document);
                return ResponseEntity.ok(savedDocument);
            }
            // Возвращаем статус 201 (Created) и сам созданный документ

        } catch (IllegalArgumentException e) {
            // Если возникла ошибка из-за некорректных данных (например, дублирование),
            // возвращаем статус 400 (Bad Request) и сообщение ошибки
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // В случае неожиданной ошибки возвращаем статус 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении документа");
        }
    }

    @PostMapping("/documents/fill")
    public ResponseEntity<?> fillDocument(
            @Valid @RequestBody InventoryDocument document) {
        try {
            // Вызываем метод сервиса для заполнения документа товарами с ненулевым остатком
            InventoryDocument filledDoc = inventoryService.fillDocumentWithItems(document);

            // Возвращаем HTTP-статус 201 (Created) и заполненный документ в теле ответа
            return ResponseEntity.status(HttpStatus.CREATED).body(filledDoc);
        } catch (IllegalArgumentException e) {
            // Если произошла ошибка, связанная с некорректными данными (например, документ не задан или отсутствуют товары с остатком),
            // возвращаем статус 400 (Bad Request) и сообщение об ошибке
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // В случае любой другой непредвиденной ошибки возвращаем статус 500 (Internal Server Error)
            // и общее сообщение об ошибке при заполнении документа
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при заполнении документа");
        }
    }
}
