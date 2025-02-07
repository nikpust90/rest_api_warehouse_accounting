package rest_api_warehouse_accounting.model.document.item;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rest_api_warehouse_accounting.model.document.OrderDocument;
import rest_api_warehouse_accounting.model.referenceBooks.Product;

@Entity
@Table(name = "order_item") // Имя таблицы в БД
@Data
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Первичный ключ
    private Long id;

    @Column(name = "quantity", nullable = false) // Количество списанного товара (обязательно)
    private int quantity;

    // Ссылка на товар (общий класс Product)
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // Внешний ключ на таблицу Product
    private Product product;

    // Ссылка на документ списания, которому принадлежит эта позиция
    @ManyToOne
    @JoinColumn(name = "order_document_id", nullable = false) // Внешний ключ на документ реализации
    private OrderDocument orderDocument;
}
