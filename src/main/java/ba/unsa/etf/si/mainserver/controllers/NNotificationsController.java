package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.models.business.AdminMerchantNotification;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.ContactInformation;
import ba.unsa.etf.si.mainserver.repositories.products.AdminMerchantNotificationRepository;
import ba.unsa.etf.si.mainserver.requests.business.MANotificationRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.business.MANotificationResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NNotificationsController {
    private final AdminMerchantNotificationRepository adminMerchantNotificationRepository;
    private final BusinessService businessService;

    public NNotificationsController(AdminMerchantNotificationRepository adminMerchantNotificationRepository, BusinessService businessService) {
        this.adminMerchantNotificationRepository = adminMerchantNotificationRepository;
        this.businessService = businessService;
    }

    @PostMapping("/openOffice")
    @Secured("ROLE_MERCHANT")
    public ResponseEntity<ApiResponse> notifyAdminToOpenNewOffice(@CurrentUser UserPrincipal userPrincipal,
                                                                  @RequestBody MANotificationRequest notificationRequest){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        ContactInformation officeInformation = new ContactInformation(
                notificationRequest.getAddress(),
                notificationRequest.getCity(),
                notificationRequest.getCountry(),
                notificationRequest.getEmail(),
                notificationRequest.getPhoneNumber()
        );
        AdminMerchantNotification adminMerchantNotification = new AdminMerchantNotification(
                business, officeInformation, notificationRequest.isOpen()
        );

        adminMerchantNotificationRepository.save(adminMerchantNotification);

        return ResponseEntity.ok(new ApiResponse("Notification succesfully sent", 200));
    }

    @GetMapping("/admin")
    @Secured("ROLE_ADMIN")
    public List<MANotificationResponse> getAllNotifications(){
        return adminMerchantNotificationRepository.findAll().stream().map(MANotificationResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/admin/unread")
    @Secured("ROLE_ADMIN")
    public List<MANotificationResponse> getAllUnReadNotifications(){
        return adminMerchantNotificationRepository
                .findAll()
                .stream()
                .filter(notification -> !notification.isRead())
                .map(MANotificationResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/admin/read")
    @Secured("ROLE_ADMIN")
    public List<MANotificationResponse> getAllReadNotifications(){
        return adminMerchantNotificationRepository
                .findAll()
                .stream()
                .filter(AdminMerchantNotification::isRead)
                .map(MANotificationResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/admin/read/{notificationId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ApiResponse> readOneNotification(@PathVariable Long notificationId){
        Optional<AdminMerchantNotification> adminMerchantNotification =
                adminMerchantNotificationRepository.findById(notificationId);

        if(!adminMerchantNotification.isPresent()){
            throw new BadParameterValueException("Notification doesn't exist");
        }

        adminMerchantNotification.get().setRead(true);
        adminMerchantNotificationRepository.save(adminMerchantNotification.get());
        return ResponseEntity.ok(new ApiResponse("Notification is read", 200));
    }
}
