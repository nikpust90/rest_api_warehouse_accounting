package rest_api_warehouse_accounting.dto.referenceBooks;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelfDto {



    //@NotEmpty(message = "Поле не может быть пустым")
    private String shelfId; // Уникальный строковый идентификатор

    //@NotEmpty(message = "Поле не может быть пустым")
    private String shelfName; //


    //@NotEmpty(message = "Поле не может быть пустым")
    private String warehouseId; // <<< Вместо объекта Warehouse

    //@NotEmpty(message = "Поле не может быть пустым")
    private List<BinDto> bins;


}
