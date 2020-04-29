package ba.unsa.etf.si.mainserver.models.products;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.business.Business;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.IOException;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String name;
    private BigDecimal price;
    private double pdv;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "discount_id", referencedColumnName = "id")
    @JsonManagedReference
    private Discount discount = null;

    private byte[] image;
    private String unit;
    private String barcode;
    private String description = "";

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="business_id", nullable=false)
    private Business business;

    public Product(String name, BigDecimal price, String unit, byte[] image, double pdv) throws IOException {
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.image = image;
        this.discount = null;
        this.pdv = pdv;
    }

    public Product(String name, BigDecimal price, String unit, byte[] image, Discount discount, double pdv) throws IOException {
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.image = image;
        this.discount = discount;
        this.pdv = pdv;
    }

    public Product(String name, BigDecimal price, String unit, String barcode, String description, double pdv) {
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.discount = null;
        this.barcode = barcode;
        this.description = description;
        this.pdv = pdv;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPdv() {
        return pdv;
    }

    public void setPdv(double pdv) {
        this.pdv = pdv;
    }
}
