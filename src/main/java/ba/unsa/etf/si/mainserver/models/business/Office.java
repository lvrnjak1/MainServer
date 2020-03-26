package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offices")
@NoArgsConstructor
@AllArgsConstructor
public class Office extends AuditModel { //ovo je poslovnica

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_info_id", referencedColumnName = "id")
    private ContactInformation contactInformation;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="business_id", nullable=false)
    private Business business;

    @OneToMany(mappedBy="office")
    private Set<CashRegister> cashRegisters;

    @OneToMany(mappedBy="office")
    private Set<OfficeProfile> officeProfiles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private EmployeeProfile manager = null;

    public Office(ContactInformation contactInformation, Business business){
        this.contactInformation = contactInformation;
        this.business = business;
        this.cashRegisters = new HashSet<>();
        this.officeProfiles = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContactInformation getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(ContactInformation contactInformation) {
        this.contactInformation = contactInformation;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Set<CashRegister> getCashRegisters() {
        return cashRegisters;
    }

    public void setCashRegisters(Set<CashRegister> cashRegisters) {
        this.cashRegisters = cashRegisters;
    }

    public Set<OfficeProfile> getOfficeProfiles() {
        return officeProfiles;
    }

    public void setOfficeProfiles(Set<OfficeProfile> officeProfiles) {
        this.officeProfiles = officeProfiles;
    }

    public EmployeeProfile getManager() {
        return manager;
    }

    public void setManager(EmployeeProfile manager) {
        this.manager = manager;
    }
}
