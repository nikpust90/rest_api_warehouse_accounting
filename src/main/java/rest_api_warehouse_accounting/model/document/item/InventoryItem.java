package rest_api_warehouse_accounting.model.document.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rest_api_warehouse_accounting.model.directory.Product;
import rest_api_warehouse_accounting.model.document.InventoryDocument;

@Entity
@Table(name = "inventory_item") // Имя таблицы в БД
@Data
@Getter
@Setter
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_document_id")
    private InventoryDocument inventoryDocument;

    // Связь с таблицей продуктов (Product)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

//    private int quantity;

    @Column(name = "quantity_plan", nullable = false) // остатки товара на дату (обязательное поле)
    private int quantityPlan;

    @Column(name = "quantity_fact", nullable = false) // факт введенный по сканеру (обязательное поле)
    private int quantityFact;

    @Column(name = "deviation", nullable = true) // разница между фактом и планом
    private int deviation;



}
