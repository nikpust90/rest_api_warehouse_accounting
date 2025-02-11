package rest_api_warehouse_accounting.repositories.document;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api_warehouse_accounting.model.document.OrderDocument;

@Repository
public interface RealizationDocumentRepository extends JpaRepository<OrderDocument, Long> {
}
