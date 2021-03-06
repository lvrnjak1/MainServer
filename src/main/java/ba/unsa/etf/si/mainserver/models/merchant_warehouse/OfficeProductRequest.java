package ba.unsa.etf.si.mainserver.models.merchant_warehouse;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import javax.persistence.*;


@Entity
@Table(name = "office_product_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OfficeProductRequest extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long officeId;

    public OfficeProductRequest(Long officeId) {
        this.officeId = officeId;
    }
}

