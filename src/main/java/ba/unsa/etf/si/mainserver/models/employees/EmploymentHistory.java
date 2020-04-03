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

    private Long employeeProfileId;

    private Long officeId;

    @Basic
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date startDate;

    @Basic
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date endDate;

    public EmploymentHistory(Long employeeProfileId, Long officeId, Date start, Date end) {
        this.employeeProfileId = employeeProfileId;
        this.officeId = officeId;
        this.startDate = start;
        this.endDate = end;
    }
}
