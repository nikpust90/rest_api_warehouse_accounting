package rest_api_warehouse_accounting.repositories.document.item;

import org.springframework.data.jpa.repository.JpaRepository;
import rest_api_warehouse_accounting.model.document.item.OutgoingItem;

public interface OutgoingItemRepository extends JpaRepository<OutgoingItem, Long> {
}
