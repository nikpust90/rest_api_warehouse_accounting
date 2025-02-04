package rest_api_warehouse_accounting.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api_warehouse_accounting.model.document.IncomingDocument;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IncomingDocumentRepository extends JpaRepository<IncomingDocument, Long> {
    Optional<IncomingDocument> findByDocumentNumberAndCreatedAt(String documentNumber, LocalDateTime createdAt);

}