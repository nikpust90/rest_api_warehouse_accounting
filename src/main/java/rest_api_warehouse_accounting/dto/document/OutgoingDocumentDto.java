package rest_api_warehouse_accounting.dto.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rest_api_warehouse_accounting.dto.document.item.OutgoingItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutgoingDocumentDto {
    private String documentNumber; // Номер документа списания

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt; // Дата создания документа

    private List<OutgoingItemDto> items; // Список позиций списания

    private String warehouseId; // Идентификатор склада
}
