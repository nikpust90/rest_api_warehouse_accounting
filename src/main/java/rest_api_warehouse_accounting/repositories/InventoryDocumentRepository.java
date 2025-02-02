package rest_api_warehouse_accounting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rest_api_warehouse_accounting.model.document.InventoryDocument;
import rest_api_warehouse_accounting.model.document.item.InventoryItem;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InventoryDocumentRepository extends JpaRepository<InventoryDocument, Long> {
    Optional<Object> findByDocumentNumberAndCreatedAt(String documentNumber, LocalDateTime documentDate);
}
