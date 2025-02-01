package rest_api_warehouse_accounting.model.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "incoming_document") // Указывает имя таблицы в БД
@Data
@Getter
@Setter
public class IncomingDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Явное указание колонки
    private Long id;

    @Column(name = "document_number", nullable = false, unique = true, length = 50) // Уникальный номер документа
    private String documentNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "created_at", nullable = false) // Дата создания документа
    private LocalDateTime createdAt;

    // Один документ может содержать несколько позиций поступления
    @OneToMany(mappedBy = "incomingDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IncomingItem> items;
}
