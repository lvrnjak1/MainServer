package ba.unsa.etf.si.mainserver.repositories.pr;

import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.Table;
import ba.unsa.etf.si.mainserver.models.pr.Reservation;
import ba.unsa.etf.si.mainserver.models.pr.ReservationStatus;
import ba.unsa.etf.si.mainserver.models.pr.ReservationStatusName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByTable_Office(Office office);
    Optional<Reservation> findByIdAndTable_Office_Id(Long reservationId, Long officeId);
    Optional<Reservation> findByEmailAndVerificationCodeAndReservationStatus_Name(String email,
                                                                                  Long code,
                                                                                  ReservationStatusName name);
    Optional<Reservation> findByVerificationCode(Long verificationCode);
    List<Reservation> findAllByTable(Table table);
    List<Reservation> findAllByTableAndReservationStatus(Table table, ReservationStatus reservationStatus);
}
