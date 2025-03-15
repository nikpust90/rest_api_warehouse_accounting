package rest_api_warehouse_accounting.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rest_api_warehouse_accounting.dto.document.OutgoingDocumentDto;
import rest_api_warehouse_accounting.dto.document.item.OutgoingItemDto;
import rest_api_warehouse_accounting.dto.referenceBooks.BinDto;
import rest_api_warehouse_accounting.dto.referenceBooks.ProductDto;
import rest_api_warehouse_accounting.model.document.OutgoingDocument;
import rest_api_warehouse_accounting.model.document.item.OutgoingItem;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Product;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;
import rest_api_warehouse_accounting.repositories.referenceBooks.WarehouseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OutgoingDocumentMapper {

    private final WarehouseRepository warehouseRepository;

    /**
     * Преобразует сущность OutgoingDocument в DTO.
     *
     * @param document Документ списания.
     * @return OutgoingDocumentDto.
     */
    public OutgoingDocumentDto toDTO(OutgoingDocument document) {
        return new OutgoingDocumentDto(
                document.getId(),
                document.getDocumentNumber(),
                document.getCreatedAt(),
                document.getItems().stream().map(this::toDTO).toList(),
                document.getWarehouse().getWarehouseId()
        );
    }

    /**
     * Преобразует список сущностей OutgoingDocument в список DTO.
     *
     * @param documents Список документов списания.
     * @return Список OutgoingDocumentDto.
     */
    public List<OutgoingDocumentDto> toDTOList(List<OutgoingDocument> documents) {
        return documents.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует сущность OutgoingItem в DTO.
     *
     * @param item Позиция списания.
     * @return OutgoingItemDto.
     */
    public OutgoingItemDto toDTO(OutgoingItem item) {
        return new OutgoingItemDto(
                new ProductDto(item.getProduct().getId(), item.getProduct().getName(), item.getProduct().getQuantity(), item.getProduct().getBarcode()),
                item.getQuantity(),
                new BinDto(item.getBin().getBinId(), item.getBin().getBinName(), item.getBin().getShelf().getShelfId(), item.getBin().getQrCode())
        );
    }

    /**
     * Преобразует DTO в сущность OutgoingDocument.
     *
     * @param dto DTO документа списания.
     * @return OutgoingDocument.
     */
    public OutgoingDocument toEntity(OutgoingDocumentDto dto) {
        OutgoingDocument document = new OutgoingDocument();
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setCreatedAt(dto.getCreatedAt());

        if (dto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findByWarehouseId(dto.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("Склад с warehouseId " + dto.getWarehouseId() + " не найден"));
            document.setWarehouse(warehouse);
        }

        return document;
    }

    /**
     * Преобразует DTO в сущность OutgoingItem.
     *
     * @param dto     DTO позиции списания.
     * @param product Продукт.
     * @param bin     Ячейка.
     * @param shelf   Стеллаж.
     * @return OutgoingItem.
     */
    public OutgoingItem toEntity(OutgoingItemDto dto, Product product, Bin bin, Shelf shelf) {
        return new OutgoingItem(product, dto.getQuantity(), bin, shelf);
    }


    /**
     * Обновляет существующую сущность OutgoingDocument на основе данных из DTO.
     *
     * @param document Существующий документ списания.
     * @param dto      DTO с новыми данными.
     * @return Обновленная сущность OutgoingDocument.
     */
    public OutgoingDocument updateEntityFromDto(OutgoingDocument document, OutgoingDocumentDto dto) {
        if (dto.getId() == null || !dto.getId().equals(document.getId())) {
            throw new IllegalArgumentException("ID документа не совпадает");
        }
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setCreatedAt(dto.getCreatedAt());

        if (dto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findByWarehouseId(dto.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("Склад с warehouseId " + dto.getWarehouseId() + " не найден"));
            document.setWarehouse(warehouse);
        }

        // Обновляем позиции документа, если они переданы в DTO
        if (dto.getItems() != null) {
            List<OutgoingItem> items = dto.getItems().stream()
                    .map(itemDto -> {
                        // Предполагаем, что Product, Bin и Shelf уже существуют в базе данных
                        Product product = new Product();
                        product.setId(itemDto.getProduct().getId());
                        product.setName(itemDto.getProduct().getName());
                        product.setQuantity(itemDto.getProduct().getQuantity());
                        product.setBarcode(itemDto.getProduct().getBarcode());

                        Bin bin = new Bin();
                        bin.setBinId(itemDto.getBin().getBinId());
                        bin.setBinName(itemDto.getBin().getBinName());
                        bin.setQrCode(itemDto.getBin().getQrCode());

                        Shelf shelf = new Shelf();
                        shelf.setShelfId(itemDto.getBin().getShelfId());

                        return toEntity(itemDto, product, bin, shelf);
                    })
                    .collect(Collectors.toList());
            document.setItems(items);
        }

        return document;
    }
}
