package rest_api_warehouse_accounting.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest_api_warehouse_accounting.dto.document.OutgoingDocumentDto;
import rest_api_warehouse_accounting.service.OutgoingService;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/outgoing/documents")
public class OutgoingController {


    private final OutgoingService outgoingService;


    /**
     * Получение списка всех документов списания товаров.
     *
     * @return ResponseEntity со списком документов или сообщением об ошибке.
     */

    @GetMapping
    public ResponseEntity<List<OutgoingDocumentDto>> getOutgoingDocuments() {
        List<OutgoingDocumentDto> documents = outgoingService.getAllOutgoingDocuments();
        return ResponseEntity.ok(documents);
    }

    /**
     * Создает новый документ списания товаров.
     *
     * @param document Объект документа реализации (передается в теле запроса)
     * @return HTTP-ответ с созданным документом или ошибкой
     */

    @PostMapping
    public ResponseEntity<OutgoingDocumentDto> createOutgoingDocument(
            @Valid @RequestBody OutgoingDocumentDto document) {
        OutgoingDocumentDto createdDocument = outgoingService.createOutgoingDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
    }

    /**
     * Получение документа списания по его ID.
     *
     * @param id Идентификатор документа списания.
     * @return ResponseEntity с документом или сообщением об ошибке.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OutgoingDocumentDto> getOutgoingDocumentById(@PathVariable Long id) {
        OutgoingDocumentDto document = outgoingService.getOutgoingDocumentById(id);
        return ResponseEntity.ok(document);
    }
}
