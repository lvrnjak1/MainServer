package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_merchant_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMerchantNotification extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "business_id", referencedColumnName = "id")
    private Business business;

    @OneToOne
    @JoinColumn(name = "contact_info_id", referencedColumnName = "id")
    ContactInformation office;

    private boolean open;
    private boolean read = false;

    public AdminMerchantNotification(Business business, ContactInformation officeInformation, boolean open) {
        this.business = business;
        this.office = officeInformation;
        this.open = open;
    }
}
