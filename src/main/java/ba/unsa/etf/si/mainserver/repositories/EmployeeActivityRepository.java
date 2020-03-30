package ba.unsa.etf.si.mainserver.repositories;

import ba.unsa.etf.si.mainserver.models.EmployeeActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeActivityRepository extends JpaRepository<EmployeeActivity,Long> {
}
