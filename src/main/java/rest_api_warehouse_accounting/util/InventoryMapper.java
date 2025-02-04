package rest_api_warehouse_accounting.util;

import org.springframework.stereotype.Component;
import rest_api_warehouse_accounting.dto.InventoryDocumentDTO;
import rest_api_warehouse_accounting.dto.InventoryItemDTO;
import rest_api_warehouse_accounting.model.document.InventoryDocument;
import rest_api_warehouse_accounting.model.document.item.InventoryItem;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryMapper {

    public InventoryDocumentDTO toDTO(InventoryDocument document) {
        return new InventoryDocumentDTO(
                document.getId(),
                document.getDocumentNumber(),
                document.getCreatedAt(),
                toItemDTOList(document.getItems())
        );
    }

    public List<InventoryDocumentDTO> toDTOList(List<InventoryDocument> documents) {
        return documents.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InventoryItemDTO toDTO(InventoryItem item) {
        return new InventoryItemDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getQuantityPlan(),
                item.getQuantityFact(),
                item.getDeviation()
        );
    }

    private List<InventoryItemDTO> toItemDTOList(List<InventoryItem> items) {
        return items.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
