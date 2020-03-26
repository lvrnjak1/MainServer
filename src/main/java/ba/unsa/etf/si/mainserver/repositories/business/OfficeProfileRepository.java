package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeProfileRepository extends JpaRepository<OfficeProfile, Long> {
}
