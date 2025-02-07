package rest_api_warehouse_accounting.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest_api_warehouse_accounting.model.document.OrderDocument;
import rest_api_warehouse_accounting.service.RealizationService;

@RestController
@RequestMapping("/api/realization")
public class RealizationController {

    @Autowired
    private RealizationService realizationService;

    /**
     * Получает список всех документов реализации.
     *
     * @return HTTP-ответ с данными или ошибкой
     */
    @GetMapping("/documents")
    public ResponseEntity<?> getRealizationDocuments() {
        try {
            // Запрашиваем список всех документов реализации через сервис
            return ResponseEntity.ok(realizationService.getAllRealizationDocuments());
        } catch (Exception e) {
            // В случае ошибки возвращаем HTTP 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении документов реализации");
        }
    }

    /**
     * Создает новый документ реализации.
     *
     * @param document Объект документа реализации (передается в теле запроса)
     * @return HTTP-ответ с созданным документом или ошибкой
     */
    @PostMapping("/documents")
    public ResponseEntity<?> createRealizationDocument(@Valid @RequestBody OrderDocument document) {
        try {
            // Передаем документ реализации в сервис для обработки
            OrderDocument createdDocument = realizationService.createRealizationDocument(document);
            // Возвращаем HTTP 201 (Created) с данными созданного документа
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
        } catch (IllegalArgumentException e) {
            // Если ошибка связана с бизнес-логикой (например, недостаточно товара), возвращаем HTTP 400 (Bad Request)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Если произошла непредвиденная ошибка, возвращаем HTTP 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при добавлении документа реализации");
        }
    }
}
