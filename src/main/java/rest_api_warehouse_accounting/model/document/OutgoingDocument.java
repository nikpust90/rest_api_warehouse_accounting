package rest_api_warehouse_accounting.model.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import rest_api_warehouse_accounting.model.document.item.OutgoingItem;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;

import java.time.LocalDateTime;
import java.util.List;


@Data

@Entity
@Table(name = "outgoing_document")
public class OutgoingDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_number", nullable = false, unique = true, length = 50)
    private String documentNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "outgoingDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OutgoingItem> items;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
}
