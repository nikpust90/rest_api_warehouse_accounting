package rest_api_warehouse_accounting.model.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rest_api_warehouse_accounting.model.Product;

@Entity
@Table(name = "incoming_item") // Имя таблицы в БД
@Data
@Getter
@Setter
public class IncomingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Первичный ключ
    private Long id;

    @Column(name = "quantity", nullable = false) // Количество поступившего товара (обязательное поле)
    private int quantity;

    // Связь с таблицей продуктов (Product)
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // Внешний ключ на продукт
    private Product product;

    // Связь с таблицей IncomingDocument
    @ManyToOne
    @JoinColumn(name = "incoming_document_id", nullable = false) // Внешний ключ на документ поступления
    @JsonIgnore // 🔥 Эта аннотация предотвратит зацикливание
    private IncomingDocument incomingDocument;
}