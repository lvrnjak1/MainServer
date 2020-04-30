package ba.unsa.etf.si.mainserver.services.pr;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.pr.ReservationStatus;
import ba.unsa.etf.si.mainserver.models.pr.ReservationStatusName;
import ba.unsa.etf.si.mainserver.repositories.pr.ReservationStatusRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationStatusService {
    private final ReservationStatusRepository reservationStatusRepository;

    public ReservationStatusService(ReservationStatusRepository reservationStatusRepository) {
        this.reservationStatusRepository = reservationStatusRepository;
    }

    public ReservationStatus getFromName(String statusName) {
        return reservationStatusRepository.findByName(
                Enum.valueOf(ReservationStatusName.class, statusName)
        ).orElseThrow(() -> new AppException("Invalid status name"));
    }
}
