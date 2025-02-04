package rest_api_warehouse_accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDTO {
    private Long id;
    private Long productId;
    private int quantityPlan;
    private int quantityFact;
    private int deviation;
}