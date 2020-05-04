package ba.unsa.etf.si.mainserver.models.merchant_warehouse;


import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import javax.persistence.*;


@Entity
@Table(name = "product_quantity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductQuantity extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "office_product_request_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private OfficeProductRequest officeProductRequest;
}
