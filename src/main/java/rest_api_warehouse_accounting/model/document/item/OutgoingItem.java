package rest_api_warehouse_accounting.model.document.item;

import jakarta.persistence.*;
import lombok.*;
import rest_api_warehouse_accounting.model.document.OutgoingDocument;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Product;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;

@Entity
@Table(name = "outgoing_item") // Имя таблицы в БД
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutgoingItem {
    public OutgoingItem(Product product, int quantity, Bin bin, Shelf shelf) {
        this.product = product;
        this.quantity = quantity;
        this.bin = bin;
        this.shelf = shelf;
    }

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
    @JoinColumn(name = "outgoing_document_id", nullable = false) // Внешний ключ на документ реализации
    private OutgoingDocument outgoingDocument;

    @ManyToOne
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    @ManyToOne
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;


}
