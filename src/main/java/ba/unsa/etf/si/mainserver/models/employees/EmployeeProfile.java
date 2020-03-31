package ba.unsa.etf.si.mainserver.models.employees;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.ContactInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
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

    @Basic
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date dateOfBirth;
    @NotNull
    private String jmbg;

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

    public EmployeeProfile(String name, String surname,
                           ContactInformation contactInformation,
                           User account, Business business) {
        this.name = name;
        this.surname = surname;
        this.contactInformation = contactInformation;
        this.account = account;
        this.business = business;
    }

    public EmployeeProfile(String name, String surname, Date date, String jmbg,
                           ContactInformation contactInformation,
                           User account, Business business){
        this.name = name;
        this.surname = surname;
        this.contactInformation = contactInformation;
        this.account = account;
        this.business = business;
        this.dateOfBirth = date;
        this.jmbg = jmbg;
    }

    public String getStringDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(getDateOfBirth());
    }
}
