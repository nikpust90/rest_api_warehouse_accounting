package rest_api_warehouse_accounting.dto.document.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rest_api_warehouse_accounting.dto.referenceBooks.BinDto;
import rest_api_warehouse_accounting.dto.referenceBooks.ProductDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutgoingItemDto {
    private ProductDto product; // Продукт
    private int quantity; // Количество списанного товара
    private BinDto bin; // Ячейка хранения
}
