package rest_api_warehouse_accounting.model.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rest_api_warehouse_accounting.model.document.item.InventoryItem;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inventory_document") // Указывает имя таблицы в БД
@Data
@Getter
@Setter
public class InventoryDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Явное указание колонки
    private Long id;

    @Column(name = "document_number", nullable = false, unique = true, length = 50) // Уникальный номер документа
    private String documentNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "created_at", nullable = false) // Дата создания документа
    private LocalDateTime createdAt;

    // Один документ может содержать несколько позиций
    @OneToMany(mappedBy = "inventoryDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<InventoryItem> items;
}
