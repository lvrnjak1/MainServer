package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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

//    @OneToOne
//    @JoinColumn(name = "contact_info_id", referencedColumnName = "id")
//    ContactInformation office;

    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;

    private boolean open;
    private boolean read = false;
    private Long officeId = null;

    public AdminMerchantNotification(Business business, String address,
                                     String city, String country, String email,
                                     String phoneNumber, boolean open) {
        this.business = business;
        this.open = open;
        this.address = address;
        this.city = city;
        this.country = country;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
