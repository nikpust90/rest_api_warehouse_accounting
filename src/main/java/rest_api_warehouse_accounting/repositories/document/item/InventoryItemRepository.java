package rest_api_warehouse_accounting.repositories.document.item;

import org.springframework.data.jpa.repository.JpaRepository;
import rest_api_warehouse_accounting.model.document.item.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {
}
