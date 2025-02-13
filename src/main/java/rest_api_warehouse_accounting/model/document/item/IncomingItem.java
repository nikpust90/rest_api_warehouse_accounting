package rest_api_warehouse_accounting.model.document.item;



import jakarta.persistence.*;
import lombok.*;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Product;
import rest_api_warehouse_accounting.model.document.IncomingDocument;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "incoming_item")
public class IncomingItem {
    public IncomingItem(Product product, int quantity, Bin bin, Shelf shelf) {
        this.product = product;
        this.quantity = quantity;
        this.bin = bin;
        this.shelf = shelf;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "incoming_document_id", nullable = false)
    private IncomingDocument incomingDocument;

    @ManyToOne
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    @ManyToOne
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;


}
