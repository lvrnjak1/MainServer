package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "offices")
@Data
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
    @JoinColumn(name="business_id", nullable=false)
    private Business business;

    @OneToMany(mappedBy="office")
    private Set<CashRegister> cashRegisters;

    @OneToMany(mappedBy="office")
    private Set<OfficeProfile> officeProfiles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private EmployeeProfile manager;
}
