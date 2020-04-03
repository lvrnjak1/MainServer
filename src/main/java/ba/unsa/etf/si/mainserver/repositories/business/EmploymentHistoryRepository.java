package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, Long> {
    Optional<EmploymentHistory> findByEmployeeProfileId(Long employeeId);

}
