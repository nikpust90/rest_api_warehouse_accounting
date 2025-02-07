package rest_api_warehouse_accounting.mappers;

import org.springframework.stereotype.Component;
import rest_api_warehouse_accounting.dto.document.InventoryDocumentDto;
import rest_api_warehouse_accounting.dto.document.item.InventoryItemDto;
import rest_api_warehouse_accounting.dto.referenceBooks.BinDto;
import rest_api_warehouse_accounting.dto.referenceBooks.ShelfDto;
import rest_api_warehouse_accounting.dto.referenceBooks.WarehouseDto;
import rest_api_warehouse_accounting.model.document.InventoryDocument;
import rest_api_warehouse_accounting.model.document.item.InventoryItem;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryMapper {

    public InventoryDocumentDto toDTO(InventoryDocument document) {
        return new InventoryDocumentDto(
                document.getId(),
                document.getDocumentNumber(),
                document.getCreatedAt(),
                toItemDTOList(document.getItems())
        );
    }

    public List<InventoryDocumentDto> toDTOList(List<InventoryDocument> documents) {
        return documents.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InventoryItemDto toDTO(InventoryItem item) {
        return new InventoryItemDto(
                item.getId(),
                item.getProduct().getId(),
                item.getQuantityPlan(),
                item.getQuantityFact(),
                item.getDeviation()
        );
    }

    private List<InventoryItemDto> toItemDTOList(List<InventoryItem> items) {
        return items.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }



}
