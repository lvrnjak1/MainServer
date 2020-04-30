package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.pr.Reservation;
import ba.unsa.etf.si.mainserver.requests.pr.ReservationRequest;
import ba.unsa.etf.si.mainserver.requests.pr.UpdateReservation;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.pr.ReservationResponse;
import ba.unsa.etf.si.mainserver.services.EmailService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.pr.ReservationService;
import ba.unsa.etf.si.mainserver.services.pr.ReservationStatusService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ReservationsController {

    private final ReservationService reservationService;
    private final OfficeService officeService;
    private final BusinessService businessService;
    private final ReservationStatusService reservationStatusService;
    private final EmailService emailService;

    public ReservationsController(ReservationService reservationService,
                                  OfficeService officeService,
                                  BusinessService businessService,
                                  ReservationStatusService reservationStatusService, EmailService emailService) {
        this.reservationService = reservationService;
        this.officeService = officeService;
        this.businessService = businessService;
        this.reservationStatusService = reservationStatusService;
        this.emailService = emailService;
    }

    //ruta da se dobiju sve rezervacije za ured
    //za nelogovanog usera pr aplikacije
    @GetMapping("/business/{businessId}/offices/{officeId}/reservations")
    public List<ReservationResponse> getAllReservationsForOfficeInBusiness(@PathVariable Long businessId,
                                                                           @PathVariable Long officeId){
        validate(businessId, officeId);
        businessService.checkIfTablesAvailable(businessService.findBusinessById(businessId));
        return reservationService.
                findAllForOffice(officeService.findByIdOrThrow(officeId))
                .stream()
                .map(reservationService::mapReservationToReservationResponse)
                .collect(Collectors.toList());
    }

    //ruta da se pošalje rezervacija
    @PostMapping("/business/{businessId}/offices/{officeId}/reservations")
    public ReservationResponse makeReservation(@PathVariable Long businessId,
                                               @PathVariable Long officeId,
                                               @RequestBody ReservationRequest reservationRequest){
        validate(businessId, officeId);
        Business business = businessService.findBusinessById(businessId);
        Office office = officeService.findByIdOrThrow(officeId);
        businessService.checkIfTablesAvailable(business);
        ReservationResponse reservation =  reservationService.makeReservation(reservationRequest);

        sendVerificationEmail(reservation, business, office);

        return reservation;
    }

    private void sendVerificationEmail(ReservationResponse reservation, Business business, Office office) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        SimpleMailMessage verificationEmail = new SimpleMailMessage();
        verificationEmail.setTo(reservation.getEmail());
        verificationEmail.setSubject("Verify your reservation");
        verificationEmail.setText("You are about to make a reservation at " + business.getName() + " at address: " +
                office.getContactInformation().getAddress() +
                "\nfor: " + reservation.getReservationDateTime().format(formatter) +
                "\n\nThis reservation isn't valid until you verify it using the code below:\n\n" +
                "Code: " + reservation.getVerificationCode() +
                "\n\nUse this code along with your email address to cancel this reservation at any point before the reserved time! \n\n" +
                "We hope you have a great time :)"
        );

        emailService.sendEmail(verificationEmail);
    }

    //ruta da se verifikuje rezervacija
    @PostMapping("/reservations/{reservationId}")
    public ApiResponse verifyReservation(@PathVariable Long reservationId,
                                                 @RequestBody UpdateReservation verifyReservationRequest){
        Reservation reservation = reservationService.findById(reservationId);
        reservationService.validateEmailAndCode(reservation,
                verifyReservationRequest.getEmail(),
                verifyReservationRequest.getVerificationCode());

        if(!reservation.getReservationStatus().getName().toString().equals("UNVERIFIED")){
            throw new AppException("You can't verify this reservation");
        }

        reservation.setReservationStatus(reservationStatusService.getFromName("VERIFIED"));
        reservationService.save(reservation);
        return new ApiResponse("Reservation successfully verified", 200);
    }

    //ruta da se obriše rezervacija
    @DeleteMapping("/reservations")
    public ApiResponse cancelReservation(@RequestBody UpdateReservation deleteReservationRequest){
        Reservation reservation = reservationService.findByEmailAndCodeAndVerified(deleteReservationRequest.getEmail(),
                deleteReservationRequest.getVerificationCode());

        reservationService.checkIfPassed(reservation);

        reservation.setReservationStatus(reservationStatusService.getFromName("CANCELED"));
        reservationService.save(reservation);
        return new ApiResponse("Reservation successfully canceled", 200);
    }

    private void validate(Long businessId, Long officeId){
        Business business = businessService.findBusinessById(businessId);
        Office office = officeService.findByIdOrThrow(officeId);
        officeService.validateBusiness(office, business);
        businessService.checkIfTablesAvailable(business);
    }
}
