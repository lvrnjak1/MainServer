package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, Long> {
    List<EmploymentHistory> findAllByEmployeeProfileIdAndOfficeId(Long employeeProfileId, Long officeId);

    List<EmploymentHistory> findAllByEmployeeProfileId(Long employeeProfileId);
}
