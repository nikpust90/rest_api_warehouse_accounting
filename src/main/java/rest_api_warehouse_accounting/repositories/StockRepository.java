package rest_api_warehouse_accounting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Product;
import rest_api_warehouse_accounting.model.regisrt.Stock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductAndBin(Product product, Bin bin);

    @Query("SELECT s FROM Stock s WHERE s.product.id IN :productIds AND s.bin.binId IN :binIds AND s.incomingDocument.id = :incomingDocumentId")
    List<Stock> findAllByProductIdsAndBinIdsAndIncomingDocument(
            @Param("productIds") List<Long> productIds,
            @Param("binIds") List<String> binIds,
            @Param("incomingDocumentId") Long incomingDocumentId
    );
}
