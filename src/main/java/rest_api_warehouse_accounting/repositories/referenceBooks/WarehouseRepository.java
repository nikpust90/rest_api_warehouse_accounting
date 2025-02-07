package rest_api_warehouse_accounting.repositories.referenceBooks;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;

import java.util.Optional;


public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByWarehouseId(String warehouseId);

    Optional<Warehouse> findByWarehouseId(String warehouseId);

}
