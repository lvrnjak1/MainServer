package ba.unsa.etf.si.mainserver.repositories.pr;

import ba.unsa.etf.si.mainserver.models.pr.ReservationStatus;
import ba.unsa.etf.si.mainserver.models.pr.ReservationStatusName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationStatusRepository extends JpaRepository<ReservationStatus, Long> {
    Optional<ReservationStatus> findByName(ReservationStatusName name);
}
