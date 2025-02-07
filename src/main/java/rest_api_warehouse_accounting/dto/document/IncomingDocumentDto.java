package rest_api_warehouse_accounting.dto.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rest_api_warehouse_accounting.dto.document.item.IncomingItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomingDocumentDto {

    private String documentNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    //@NotNull(message = "Дата создания документа обязательна")
    private LocalDateTime createdAt;

    private List<IncomingItemDto> items;

    private String warehouseId; // Идентификатор склада
}
