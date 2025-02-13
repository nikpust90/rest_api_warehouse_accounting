package rest_api_warehouse_accounting.repositories.document;


import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api_warehouse_accounting.model.document.OutgoingDocument;

import java.time.LocalDateTime;

@Repository
public interface OutgoingDocumentRepository extends JpaRepository<OutgoingDocument, Long> {
    boolean existsByDocumentNumberAndCreatedAt(@NotEmpty(message = "Номер документа не может быть пустым") String documentNumber, @NotNull(message = "Дата создания документа обязательна") LocalDateTime createdAt);
}
