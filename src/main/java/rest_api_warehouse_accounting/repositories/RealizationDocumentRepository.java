package rest_api_warehouse_accounting.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api_warehouse_accounting.model.document.RealizationDocument;

@Repository
public interface RealizationDocumentRepository extends JpaRepository<RealizationDocument, Long> {
}
