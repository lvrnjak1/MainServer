package ba.unsa.etf.si.mainserver.models.transactions;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.products.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "receipt_items")
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItem extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long productId;
    private String productName;
    private String barcode;
    private BigDecimal price;
    private int discountPercentage;
    private String unit;

    private double quantity;

    public ReceiptItem(Product product, double quantity){
        this.productId = product.getId();
        this.productName = product.getName();
        this.barcode = product.getBarcode();
        this.price = product.getPrice();
        if(product.getDiscount() == null){
            this.discountPercentage = 0;
        }
        else this.discountPercentage = product.getDiscount().getPercentage();
        this.quantity = quantity;
        this.unit = product.getUnit();
    }
}
