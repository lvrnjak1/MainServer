package ba.unsa.etf.si.mainserver.services.pr;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.Table;
import ba.unsa.etf.si.mainserver.models.pr.Reservation;
import ba.unsa.etf.si.mainserver.models.pr.ReservationStatus;
import ba.unsa.etf.si.mainserver.repositories.pr.ReservationRepository;
import ba.unsa.etf.si.mainserver.requests.pr.ReservationRequest;
import ba.unsa.etf.si.mainserver.responses.pr.ReservationResponse;
import ba.unsa.etf.si.mainserver.services.business.TableService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationStatusService reservationStatusService;
    private final TableService tableService;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationStatusService reservationStatusService, TableService tableService) {
        this.reservationRepository = reservationRepository;
        this.reservationStatusService = reservationStatusService;
        this.tableService = tableService;
    }

    public List<Reservation> findAllForOffice(Office office) {
        return reservationRepository.findAllByTable_Office(office);
    }

    public ReservationResponse mapReservationToReservationResponse(Reservation reservation){
        return new ReservationResponse(reservation.getId(),
                reservation.getReservationStatus().getName().toString(),
                TableService.mapTableToTableResponse(reservation.getTable()),
                reservation.getName(),
                reservation.getSurname(),
                reservation.getEmail(),
                reservation.getReservationDateTime(),
                reservation.getVerificationCode());
    }

    public Reservation findByIdAndOffice(Long reservationId, Long officeId) {
        return reservationRepository.findByIdAndTable_Office_Id(reservationId, officeId)
                .orElseThrow(() -> new ResourceNotFoundException("This reservation doesn't exist"));
    }

    public void validateEmailAndCode(Reservation reservation, String email, Long verificationCode) {
        if(!(reservation.getEmail().equals(email) && reservation.getVerificationCode().equals(verificationCode))){
            throw new AppException("Incorrect email and verification code combination!");
        }
    }

    public void save(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public Reservation findByEmailAndCodeAndVerified(String email, Long verificationCode) {
        return reservationRepository.findByEmailAndVerificationCodeAndReservationStatus_Name(email,
                verificationCode,
                reservationStatusService.getFromName("VERIFIED").getName())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation doesn't exist"));
    }

    public ReservationResponse makeReservation(ReservationRequest reservationRequest) {
        Reservation reservation = mapReservationRequestToReservation(reservationRequest);
        checkIfPassed(reservation, "You can't make a reservation in past");
        checkAvailability(reservation);
        reservationRepository.save(reservation);
        return mapReservationToReservationResponse(reservation);
    }

    private void checkAvailability(Reservation reservation) {
        Long durationMins = 60L; //ovo je koliko minuta traje rezervacija business.getDurationMins()
        List<Reservation> reservationsForTable =
                findAllByTableAndStatus(reservation.getTable(), "VERIFIED");
        if (reservationsForTable.stream().anyMatch(r -> timesIntersect(r.getReservationDateTime(),
                reservation.getReservationDateTime(),
                durationMins))){
            throw new AppException("Table not available for reservation");
        }
    }

    private List<Reservation> findAllByTableAndStatus(Table table, String statusName){
        ReservationStatus reservationStatus = reservationStatusService.getFromName(statusName);
        return reservationRepository.findAllByTableAndReservationStatus(table, reservationStatus);
    }

    private static boolean timesIntersect(LocalDateTime dateTime1, LocalDateTime dateTime2, Long durationMins) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        System.out.println(dateTime1.format(formatter));
//        System.out.println((dateTime1.plusMinutes(durationMins)).format(formatter));
//        System.out.println(dateTime2.format(formatter));
//        System.out.println((dateTime2.plusMinutes(durationMins)).format(formatter));
        return ((dateTime2.isAfter(dateTime1) || dateTime2.isEqual(dateTime1))
                && (dateTime2.isBefore(dateTime1.plusMinutes(durationMins))))
                || (dateTime1.isBefore(dateTime2.plusMinutes(durationMins))
                && (dateTime1.isAfter(dateTime2) || dateTime1.isEqual(dateTime2)));
    }

    private Reservation mapReservationRequestToReservation(ReservationRequest reservationRequest) {
        Long verificationCode = generateUniqueCode();
        ReservationStatus reservationStatus = reservationStatusService.getFromName("UNVERIFIED");
        Table table = tableService.findById(reservationRequest.getTableId());
        return new Reservation(reservationStatus, table, reservationRequest.getName(),
                reservationRequest.getSurname(), reservationRequest.getEmail(),
                reservationRequest.getReservationDateTime(), verificationCode);
    }

    private boolean isUniqueCode(Long verificationCode) {
        return !reservationRepository.findByVerificationCode(verificationCode).isPresent();
    }


    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation doesn't exist"));
    }

    public void checkIfPassed(Reservation reservation, String message) {
        if(isInPast(reservation)){
            throw new AppException(message);
        }
    }

    public Long generateUniqueCode(){
        Long verificationCode = generateNewRandomCode();

        while (!isUniqueCode(verificationCode)){
            verificationCode = generateNewRandomCode();
        }
        //TODO fix this loop
        return verificationCode;
    }

    private static Long generateNewRandomCode() {
        long lowerBound = 100000L;
        long upperBound = 999999L;
        return lowerBound + (long) (Math.random() * (upperBound - lowerBound));
    }

    public void regenerateCodeAndSave(Reservation reservation) {
        Long verificationCode = generateUniqueCode();
        reservation.setVerificationCode(verificationCode);
        reservationRepository.save(reservation);
    }

    public boolean isInPast(Reservation reservation) {
        return reservation.getReservationDateTime().isBefore(LocalDateTime.now());
    }
}
