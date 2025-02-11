package rest_api_warehouse_accounting.model.referenceBooks;

import jakarta.persistence.*;
import lombok.*;
import rest_api_warehouse_accounting.model.document.item.IncomingItem;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shelf")
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Автоинкрементный ID для базы данных

    private String shelfId; // Уникальный строковый идентификатор

    private String shelfName; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bin> bins = new ArrayList<>();

//    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<IncomingItem> incomingItems = new ArrayList<>();
}
