package rest_api_warehouse_accounting.dto.referenceBooks;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDto {

    //@NotEmpty(message = "Поле не может быть пустым")
    private String warehouseId; // Уникальный строковый идентификатор склада

    //@NotEmpty(message = "Поле не может быть пустым")
    private String warehouseName;

    //NotEmpty(message = "Поле не может быть пустым")
    private List<ShelfDto> shelves;


}
