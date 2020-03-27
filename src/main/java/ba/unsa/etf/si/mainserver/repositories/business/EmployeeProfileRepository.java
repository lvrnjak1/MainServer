package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    Optional<EmployeeProfile> findByContactInformation_EmailOrAccount_UsernameOrAccount_IdOrId(String email,String username,Long userId, Long employeeId);

    Optional<EmployeeProfile> findByAccountId(Long accountId);

    List<EmployeeProfile> findAllByBusinessId(Long businessId);
}
