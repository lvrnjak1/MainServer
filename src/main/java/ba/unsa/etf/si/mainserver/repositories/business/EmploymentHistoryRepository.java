package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, Long> {
    List<EmploymentHistory> findAllByEmployeeProfileIdAndOfficeIdAndRole(Long employeeProfileId, Long officeId, String role);
    List<EmploymentHistory> findAllByOfficeId(Long officeId);
    List<EmploymentHistory> findAllByEmployeeProfileIdAndRole(Long employeeProfileId, String role);
    List<EmploymentHistory> findAllByEmployeeProfileId(Long employeeProfileId);

}
