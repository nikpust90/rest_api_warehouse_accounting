package rest_api_warehouse_accounting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rest_api_warehouse_accounting.model.document.item.IncomingItem;

public interface IncomingItemRepository extends JpaRepository<IncomingItem, Long> {
}
