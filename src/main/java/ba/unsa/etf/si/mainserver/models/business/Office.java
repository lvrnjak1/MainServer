package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    public Office(ContactInformation contactInformation, Business business){
        this.contactInformation = contactInformation;
        this.business = business;
    }

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

    public String getStringStart(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(getWorkDayStart());
    }

    public String getStringEnd(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(getWorkDayEnd());
    }
}
