package rest_api_warehouse_accounting.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest_api_warehouse_accounting.dto.referenceBooks.BinDto;
import rest_api_warehouse_accounting.dto.referenceBooks.ShelfDto;
import rest_api_warehouse_accounting.dto.referenceBooks.WarehouseDto;
import rest_api_warehouse_accounting.mappers.WarehouseMapper;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;
import rest_api_warehouse_accounting.repositories.referenceBooks.WarehouseRepository;
import rest_api_warehouse_accounting.util.ExceptionHandlerUtil;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseService {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    /**
     * Создает склад и связанные с ним стеллажи и ячейки на основе данных, полученных из JSON.
     *
     * @param warehouseDto объект данных склада, сформированный на основе JSON с формы
     * @return сохраненный объект склада (с вложенными сущностями)
     */
    public WarehouseDto createWarehouse(@Valid WarehouseDto warehouseDto) {
        validateWarehouseDto(warehouseDto);

        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName(warehouseDto.getWarehouseName());
        warehouse.setWarehouseId(warehouseDto.getWarehouseId());

        List<Shelf> shelves = mapShelves(warehouseDto, warehouse);
        warehouse.setShelves(shelves);

        // Вызов через глобальный обработчик ошибок
        Warehouse savedWarehouse = ExceptionHandlerUtil.handleDatabaseOperation(
                () -> warehouseRepository.save(warehouse),
                "Ошибка при сохранении склада"
        );

        logger.info("Склад успешно создан: {}", savedWarehouse.getWarehouseId());
        return warehouseMapper.toDTO(savedWarehouse);
    }

    /**
     * Валидация входных данных склада
     */
    private void validateWarehouseDto(WarehouseDto warehouseDto) {
        if (warehouseDto == null) {
            throw new IllegalArgumentException("WarehouseDto не может быть null");
        }
        if (warehouseDto.getWarehouseName() == null || warehouseDto.getWarehouseName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название склада (warehouseName) обязательно");
        }
        if (warehouseRepository.existsByWarehouseId(warehouseDto.getWarehouseId())) {
            throw new IllegalStateException("Склад с таким ID уже существует");
        }
    }

    /**
     * Преобразование DTO полок в сущности
     */
    private List<Shelf> mapShelves(WarehouseDto warehouseDto, Warehouse warehouse) {
        List<ShelfDto> shelfDtos = Optional.ofNullable(warehouseDto.getShelves()).orElse(List.of());
        List<Shelf> shelves = new ArrayList<>();

        for (ShelfDto shelfDto : shelfDtos) {
            validateShelfDto(shelfDto);

            Shelf shelf = new Shelf();
            shelf.setShelfName(shelfDto.getShelfName());
            shelf.setShelfId(shelfDto.getShelfId());
            shelf.setWarehouse(warehouse);

            List<Bin> bins = mapBins(shelfDto, shelf);
            shelf.setBins(bins);

            shelves.add(shelf);
        }
        return shelves;
    }

    /**
     * Преобразование DTO ячеек в сущности
     */
    private List<Bin> mapBins(ShelfDto shelfDto, Shelf shelf) {
        List<BinDto> binDtos = Optional.ofNullable(shelfDto.getBins()).orElse(List.of());
        List<Bin> bins = new ArrayList<>();

        for (BinDto binDto : binDtos) {
            validateBinDto(binDto);

            Bin bin = new Bin();
            bin.setBinName(binDto.getBinName());
            bin.setBinId(binDto.getBinId());
            bin.setQrCode(binDto.getQrCode());
            bin.setShelf(shelf);

            bins.add(bin);
        }
        return bins;
    }

    /**
     * Валидация данных полки
     */
    private void validateShelfDto(ShelfDto shelfDto) {
        if (shelfDto.getShelfName() == null || shelfDto.getShelfName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название стеллажа (shelfName) обязательно");
        }
    }

    /**
     * Валидация данных ячейки
     */
    private void validateBinDto(BinDto binDto) {
        if (binDto.getBinName() == null || binDto.getBinName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название ячейки (binName) обязательно");
        }
    }


}

