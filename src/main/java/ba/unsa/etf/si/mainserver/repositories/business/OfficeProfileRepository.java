package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfficeProfileRepository extends JpaRepository<OfficeProfile, Long> {
    List<OfficeProfile> findAllByOfficeIdAndOffice_BusinessId(Long officeId, Long businessId);

    Optional<OfficeProfile> findByEmployee_Id(Long employeeId);
}
