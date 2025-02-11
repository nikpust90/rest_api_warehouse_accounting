package rest_api_warehouse_accounting.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest_api_warehouse_accounting.dto.document.InventoryDocumentDto;
import rest_api_warehouse_accounting.model.document.InventoryDocument;
import rest_api_warehouse_accounting.service.InventoryService;

import java.util.List;

// Аннотация @RestController указывает, что данный класс является REST-контроллером
// и все его методы будут возвращать JSON-ответы.
@RestController

// @RequestMapping указывает базовый URL для всех эндпоинтов этого контроллера.
// Все запросы, начинающиеся с "/api/inventory", будут обрабатываться в этом классе.
@RequestMapping("/api/inventory/documents")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @GetMapping
    public ResponseEntity<List<InventoryDocumentDto>> getInventoryDocuments() {
        List<InventoryDocumentDto> documents = inventoryService.getAllInventoryDocuments();
        return ResponseEntity.ok(documents);
    }

    /**
     * Создание нового документа инвентаризации.
     *
     * @param document объект InventoryDocument из тела запроса.
     * @return созданный документ.
     */
    @PostMapping
    public ResponseEntity<InventoryDocument> createInventoryDocument(
            @Valid @RequestBody InventoryDocument document) {
        InventoryDocument createdDocument = inventoryService.createInventoryDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
    }

    /**
     * Обновление существующего документа инвентаризации.
     *
     * @param document объект InventoryDocument из тела запроса.
     * @return обновленный документ.
     */
    @PutMapping
    public ResponseEntity<InventoryDocument> updateInventoryDocument(
            @Valid @RequestBody InventoryDocument document) {
        InventoryDocument updatedDocument = inventoryService.updateInventoryDocument(document);
        return ResponseEntity.ok(updatedDocument);
    }

    /**
     * Заполнение документа товарами с ненулевым остатком.
     *
     * @param document объект InventoryDocument из тела запроса.
     * @return заполненный документ.
     */
    @PostMapping("/fill")
    public ResponseEntity<InventoryDocument> fillDocument(
            @Valid @RequestBody InventoryDocument document) {
        InventoryDocument filledDoc = inventoryService.fillDocumentWithItems(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(filledDoc);
    }
}
