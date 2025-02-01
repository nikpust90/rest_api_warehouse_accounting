package rest_api_warehouse_accounting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rest_api_warehouse_accounting.model.Product;
import rest_api_warehouse_accounting.model.document.IncomingItem;

public interface IncomingItemRepository extends JpaRepository<IncomingItem, Long> {
}
