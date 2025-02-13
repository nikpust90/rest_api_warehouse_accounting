package rest_api_warehouse_accounting.model.referenceBooks;

import jakarta.persistence.*;
import lombok.*;
import rest_api_warehouse_accounting.model.document.item.IncomingItem;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bin")
public class Bin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Автоинкрементный ID для базы данных

    private String binId; // Уникальный строковый идентификатор ячейки

    private String binName;

    private String qrCode; // Поле для QR-кода

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

//    @OneToMany(mappedBy = "bin", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<IncomingItem> incomingItems = new ArrayList<>();
}

