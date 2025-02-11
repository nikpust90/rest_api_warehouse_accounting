package rest_api_warehouse_accounting.dto.referenceBooks;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinDto {


    //@NotEmpty(message = "Поле не может быть пустым")
    private String binId; // Уникальный строковый идентификатор ячейки

    //@NotEmpty(message = "Поле не может быть пустым")
    private String binName;

    private String shelfId; // Стеллаж, к которому привязана ячейка

    //@NotEmpty(message = "Поле не может быть пустым")
    private String qrCode; // Поле для QR-кода


}