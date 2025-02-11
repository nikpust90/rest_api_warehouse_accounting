package rest_api_warehouse_accounting.model.regisrt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rest_api_warehouse_accounting.model.document.IncomingDocument;
import rest_api_warehouse_accounting.model.document.OrderDocument;
import rest_api_warehouse_accounting.model.referenceBooks.Bin;
import rest_api_warehouse_accounting.model.referenceBooks.Product;
import rest_api_warehouse_accounting.model.referenceBooks.Shelf;
import rest_api_warehouse_accounting.model.referenceBooks.Warehouse;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

    @ManyToOne(optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "incoming_document_id")
    private IncomingDocument incomingDocument;

    @ManyToOne
    @JoinColumn(name = "order_document_id")
    private OrderDocument orderDocument;
}
