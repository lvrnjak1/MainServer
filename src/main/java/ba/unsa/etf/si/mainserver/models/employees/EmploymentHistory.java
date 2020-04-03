package ba.unsa.etf.si.mainserver.models.employees;

import ba.unsa.etf.si.mainserver.models.business.Office;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "employment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeProfile employeeProfile;

    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @Basic
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date startDate;

    @Basic
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date endDate;

    public EmploymentHistory(EmployeeProfile employeeProfile, Office office, Date start, Date end) {
        this.employeeProfile = employeeProfile;
        this.office = office;
        this.startDate = start;
        this.endDate = end;
    }
}
