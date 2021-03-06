package ba.unsa.etf.si.mainserver.models.employees;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "employee_activities")
public class EmployeeActivity extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_profile_id", referencedColumnName = "id")
    private EmployeeProfile employeeProfile;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private User account;

    public EmployeeActivity(EmployeeProfile employeeProfile, User user) {
        this.employeeProfile = employeeProfile;
        this.account = user;
    }
}
