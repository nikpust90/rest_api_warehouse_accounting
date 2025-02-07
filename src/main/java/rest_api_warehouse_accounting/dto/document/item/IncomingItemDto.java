package rest_api_warehouse_accounting.dto.document.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rest_api_warehouse_accounting.dto.referenceBooks.BinDto;
import rest_api_warehouse_accounting.dto.referenceBooks.ProductDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomingItemDto {
    //@NotNull(message = "Продукт обязателен")
    private ProductDto product;

    //@Min(value = 1, message = "Количество товара должно быть больше 0")
    private int quantity;

    //@NotNull(message = "Ячейка хранения обязательна")
    private BinDto bin;


}
