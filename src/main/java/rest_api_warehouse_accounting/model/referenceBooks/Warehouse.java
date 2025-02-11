package rest_api_warehouse_accounting.model.referenceBooks;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Автоинкрементный ID для базы данных

    private String warehouseId; // Уникальный строковый идентификатор склада

    private String warehouseName;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shelf> shelves = new ArrayList<>();
}

