package ba.unsa.etf.si.mainserver.models.transactions;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.products.Product;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "receipt_items")
public class ReceiptItem extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long productId;
    private String productName;
    private double pdv;
    private String barcode;
    private BigDecimal price;
    private int discountPercentage;
    private String unit;
    private double quantity;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    public ReceiptItem(){}

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
        this.pdv = product.getPdv();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public double getPdv() {
        return pdv;
    }

    public void setPdv(double pdv) {
        this.pdv = pdv;
    }
}
