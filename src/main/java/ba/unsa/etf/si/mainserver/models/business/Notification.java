package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="business_id", referencedColumnName = "id")
    private Business business;

    @ManyToOne
    @JoinColumn(name="employee_id", referencedColumnName = "id")
    private EmployeeProfile employeeProfile;

    private boolean hired;
    private boolean read = false;

    @Basic
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date date;

    @Basic
    @Temporal(TemporalType.TIME)
    @NotNull
    private Date time;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public boolean isHired() {
        return hired;
    }

    public void setHired(boolean hired) {
        this.hired = hired;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public EmployeeProfile getEmployeeProfile() {
        return employeeProfile;
    }

    public void setEmployeeProfile(EmployeeProfile employeeProfile) {
        this.employeeProfile = employeeProfile;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Notification(Business business, EmployeeProfile employeeProfile, boolean hired,
                        Date date, Date time){
        this.business = business;
        this.employeeProfile = employeeProfile;
        this.hired = hired;
        this.date = date;
        this.time = time;
    }

    public String getStringDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(getDate());
    }

    public String getStringTime(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(getTime());
    }
}

