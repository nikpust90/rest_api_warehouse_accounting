package rest_api_warehouse_accounting.mappers;

import org.springframework.stereotype.Component;
import rest_api_warehouse_accounting.dto.referenceBooks.BinDto;
import rest_api_warehouse_accounting.dto.referenceBooks.ShelfDto;
import rest_api_warehouse_accounting.dto.referenceBooks.WarehouseDto;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehouseMapper {

    public WarehouseDto toDTO(Warehouse warehouse) {
        return new WarehouseDto(
                warehouse.getWarehouseId(),
                warehouse.getWarehouseName(),
                // Используем стрим для преобразования списка стеллажей в список ShelfDto
                warehouse.getShelves() != null ? warehouse.getShelves().stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList()) : Collections.emptyList()
        );
    }

    public ShelfDto toDTO(Shelf shelf) {
        return new ShelfDto(
                shelf.getShelfId(),
                shelf.getShelfName(),
                shelf.getWarehouse().getWarehouseId(),
                // Используем стрим для преобразования списка ячеек в список BinDto
                shelf.getBins() != null ? shelf.getBins().stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList()) : Collections.emptyList()
        );
    }

    public BinDto toDTO(Bin bin) {
        return new BinDto(
                bin.getBinId(),
                bin.getBinName(),
                bin.getQrCode(),
                bin.getShelf().getShelfId()
        );
    }


}
