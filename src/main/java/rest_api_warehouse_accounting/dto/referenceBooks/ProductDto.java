package rest_api_warehouse_accounting.dto.referenceBooks;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private int quantity;
    private String barcode; // Штрихкод (опционально, если нужно передавать извне)
}
