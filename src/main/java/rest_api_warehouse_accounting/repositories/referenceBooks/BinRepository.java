package rest_api_warehouse_accounting.repositories.referenceBooks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;

import java.util.List;

public interface BinRepository extends JpaRepository<Bin, Long> {
    @Query("SELECT b FROM Bin b JOIN FETCH b.shelf WHERE b.binId IN :binIds")
    List<Bin> findAllByBinIdIn(@Param("binIds") List<String> binIds);
}
