package ba.unsa.etf.si.mainserver.models.products;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

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
    //image

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "discount_id", referencedColumnName = "id")
    private Discount discount;

    private byte[] image;
    private String unit;

//    @ManyToMany
//    @JoinTable(
//            name = "office_inventory",
//            joinColumns = @JoinColumn(name = "office_id"),
//            inverseJoinColumns = @JoinColumn(name = "product_id"))
//    private Set<Office> offices;

//    u office klasu napisati
//    @ManyToMany
//    @ManyToMany(mappedBy = "offices")
//    private Set<Product> products;
    //mora i quantity da se doda nece ici bas ovako
    //ima ovdje ispod https://www.baeldung.com/jpa-many-to-many

}
