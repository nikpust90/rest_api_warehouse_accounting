package rest_api_warehouse_accounting.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rest_api_warehouse_accounting.dto.document.IncomingDocumentDto;
import rest_api_warehouse_accounting.dto.document.InventoryDocumentDto;
import rest_api_warehouse_accounting.dto.document.item.IncomingItemDto;
import rest_api_warehouse_accounting.dto.referenceBooks.BinDto;
import rest_api_warehouse_accounting.dto.referenceBooks.ProductDto;
import rest_api_warehouse_accounting.model.document.IncomingDocument;
import rest_api_warehouse_accounting.model.document.InventoryDocument;
import rest_api_warehouse_accounting.model.document.item.IncomingItem;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Product;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;
import rest_api_warehouse_accounting.repositories.referenceBooks.WarehouseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IncomingDocumentMapper {

    private final WarehouseRepository warehouseRepository;
    public IncomingDocumentDto toDTO(IncomingDocument document) {
        return new IncomingDocumentDto(
                document.getDocumentNumber(),
                document.getCreatedAt(),
                document.getItems().stream().map(this::toDTO).toList(),
                document.getWarehouse().getWarehouseId()
        );
    }

    public List<IncomingDocumentDto> toDTOList(List<IncomingDocument> documents) {
        return documents.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    public IncomingItemDto toDTO(IncomingItem item) {
        return new IncomingItemDto(
                new ProductDto(item.getProduct().getId(), item.getProduct().getName(), item.getProduct().getQuantity(), item.getProduct().getBarcode()), // ✅ Преобразуем `Product` в `ProductDto`
                item.getQuantity(),
                new BinDto(item.getBin().getBinId(), item.getBin().getBinName(), item.getBin().getShelf().getShelfId(), item.getBin().getQrCode()) // ✅ Преобразуем `Bin` в `BinDto`
        );
    }

    public IncomingDocument toEntity(IncomingDocumentDto dto) {
        IncomingDocument document = new IncomingDocument();
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setCreatedAt(dto.getCreatedAt());

        if (dto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findByWarehouseId(dto.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("Склад с warehouseId " + dto.getWarehouseId() + " не найден"));
            document.setWarehouse(warehouse);
        }

        return document;
    }

    public IncomingItem toEntity(IncomingItemDto dto, Product product, Bin bin, Shelf shelf) {
        return new IncomingItem(product, dto.getQuantity(), bin, shelf);
    }
}
