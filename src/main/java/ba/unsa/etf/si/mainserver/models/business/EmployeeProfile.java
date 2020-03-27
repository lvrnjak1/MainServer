package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "employee_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfile extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_info_id", referencedColumnName = "id")
    private ContactInformation contactInformation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private User account;

    private String name;
    private String surname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Business business;

    public EmployeeProfile(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public EmployeeProfile(String name, String surname, ContactInformation contactInformation,User account) {
        this.name = name;
        this.surname = surname;
        this.contactInformation = contactInformation;
        this.account = account;
    }

    public EmployeeProfile(String name, String surname, ContactInformation contactInformation, User account, Business business) {
        this.name = name;
        this.surname = surname;
        this.contactInformation = contactInformation;
        this.account = account;
        this.business = business;
    }
}
