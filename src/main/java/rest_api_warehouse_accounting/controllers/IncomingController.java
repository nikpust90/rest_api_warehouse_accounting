package rest_api_warehouse_accounting.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest_api_warehouse_accounting.dto.document.IncomingDocumentDto;
import rest_api_warehouse_accounting.service.IncomingService;

import java.util.List;

// Аннотация @RestController указывает, что данный класс является REST-контроллером
// и все его методы будут возвращать JSON-ответы.
@RestController

// @RequestMapping указывает базовый URL для всех эндпоинтов этого контроллера.
// Все запросы, начинающиеся с "/api/incoming", будут обрабатываться в этом классе.
@RequestMapping("/api/incoming/documents")
@RequiredArgsConstructor
public class IncomingController {

    // Автоматически внедряем сервис IncomingService, который отвечает за бизнес-логику

    private final IncomingService incomingService;


    /**
     * Получение списка всех документов поступления товаров.
     *
     * @return ResponseEntity со списком документов или сообщением об ошибке.
     */

    @GetMapping
    public ResponseEntity<List<IncomingDocumentDto>> getIncomingDocuments() {
        List<IncomingDocumentDto> documents = incomingService.getAllIncomingDocuments();
        return ResponseEntity.ok(documents);
    }

    /**
     * Создание нового документа поступления товаров.
     *
     * @param document - объект IncomingDocument, который приходит в теле запроса.
     * @return ResponseEntity с созданным документом или сообщением об ошибке.
     */
    @PostMapping
    public ResponseEntity<IncomingDocumentDto> createIncomingDocument(
            @Valid @RequestBody IncomingDocumentDto document) {
        IncomingDocumentDto createdDocument = incomingService.createIncomingDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
    }

    /**
     * Обновление существующего документа инвентаризации.
     *
     * @param document объект InventoryDocument из тела запроса.
     * @return обновленный документ.
     */
//    @PutMapping
//    public ResponseEntity<IncomingDocumentDto> updateIncomingDocument(
//            @Valid @RequestBody InventoryDocument document) {
//        IncomingDocumentDto updatedDocument = incomingService.updateInventoryDocument(document);
//        return ResponseEntity.ok(updatedDocument);
//    }
}
