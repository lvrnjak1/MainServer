package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.business.Business;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Long> {
}
