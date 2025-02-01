package rest_api_warehouse_accounting.model.document;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rest_api_warehouse_accounting.model.Product;

@Entity
@Table(name = "realization_item") // Имя таблицы в БД
@Data
@Getter
@Setter
public class RealizationItem {
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
    @JoinColumn(name = "realization_document_id", nullable = false) // Внешний ключ на документ реализации
    private RealizationDocument realizationDocument;
}
