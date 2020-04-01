package ba.unsa.etf.si.mainserver.models.transactions;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.products.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "receipt_items")
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItem extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double quantity;

    public ReceiptItem(Product product, double quantity){
        this.product = product;
        this.quantity = quantity;
    }
}
