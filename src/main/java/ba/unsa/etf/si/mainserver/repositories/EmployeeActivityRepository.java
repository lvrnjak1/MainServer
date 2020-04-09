package ba.unsa.etf.si.mainserver.repositories;

import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeActivityRepository extends JpaRepository<EmployeeActivity,Long> {
    Optional<EmployeeActivity> findByEmployeeProfile(EmployeeProfile employeeProfile);
    Optional<EmployeeActivity> findByAccount_Id(Long accountId);
}
