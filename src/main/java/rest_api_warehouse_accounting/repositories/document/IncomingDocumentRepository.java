package rest_api_warehouse_accounting.repositories.document;


import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api_warehouse_accounting.model.document.IncomingDocument;

import java.time.LocalDateTime;
import java.util.Optional;


public interface IncomingDocumentRepository extends JpaRepository<IncomingDocument, Long> {
    Optional<IncomingDocument> findByDocumentNumberAndCreatedAt(String documentNumber, LocalDateTime createdAt);

    //boolean existsByDocumentNumberAndCreatedAt(void attr0, void attr1);

    boolean existsByDocumentNumberAndCreatedAt(@NotEmpty(message = "Номер документа не может быть пустым") String documentNumber, @NotNull(message = "Дата создания документа обязательна") LocalDateTime createdAt);
}