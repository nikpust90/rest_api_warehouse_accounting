package rest_api_warehouse_accounting.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rest_api_warehouse_accounting.dto.referenceBooks.WarehouseDto;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;
import rest_api_warehouse_accounting.service.WarehouseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private  final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseDto> createWarehouse(
            @Valid @RequestBody WarehouseDto warehouseDto) {
        // Здесь выполняется валидация и сохранение данных в БД.
        WarehouseDto warehouseSave = warehouseService.createWarehouse(warehouseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseSave);
    }
}
