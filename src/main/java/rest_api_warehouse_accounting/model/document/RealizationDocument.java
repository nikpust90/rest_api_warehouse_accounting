package rest_api_warehouse_accounting.model.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rest_api_warehouse_accounting.model.document.item.RealizationItem;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "realization_document") // Имя таблицы в БД
@Data
@Getter
@Setter
public class RealizationDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Первичный ключ
    private Long id;

    @Column(name = "document_number", nullable = false, unique = true) // Номер документа реализации (уникальный)
    private String documentNumber;

    @Column(name = "created_at", nullable = false) // Дата создания документа
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Один документ может содержать несколько позиций списания
    @OneToMany(mappedBy = "realizationDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RealizationItem> items;
}
