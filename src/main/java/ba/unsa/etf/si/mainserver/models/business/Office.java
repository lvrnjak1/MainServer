package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.Language;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private EmployeeProfile manager;

    @Basic
    @Temporal(TemporalType.TIME)
    private Date workDayStart;

    @Basic
    @Temporal(TemporalType.TIME)
    private Date workDayEnd;

    private int maxNumberCashRegisters = 5;

    @Enumerated(EnumType.STRING)
    @Column(length = 60)
    private Language languageName = Language.ENGLISH;

    public Office(ContactInformation contactInformation, Business business, Date start, Date end){
        this.contactInformation = contactInformation;
        this.business = business;
        this.workDayStart = start;
        this.workDayEnd = end;
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

    public EmployeeProfile getManager() {
        return manager;
    }

    public void setManager(EmployeeProfile manager) {
        this.manager = manager;
    }

    public Date getWorkDayStart() {
        return workDayStart;
    }

    public void setWorkDayStart(Date workDayStart) {
        this.workDayStart = workDayStart;
    }

    public Date getWorkDayEnd() {
        return workDayEnd;
    }

    public void setWorkDayEnd(Date workDayEnd) {
        this.workDayEnd = workDayEnd;
    }

    public int getMaxNumberCashRegisters() {
        return maxNumberCashRegisters;
    }

    public void setMaxNumberCashRegisters(int max_number_cashRegisters) {
        this.maxNumberCashRegisters = max_number_cashRegisters;
    }

    public Language getLanguageName() {
        return languageName;
    }

    public void setLanguageName(Language language) {
        this.languageName = language;
    }

    public void setLanguage(String language) {
        this.languageName = Language.valueOf(language.toUpperCase());
    }

    public String getStringStart(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(getWorkDayStart());
    }

    public String getStringEnd(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(getWorkDayEnd());
    }
}
