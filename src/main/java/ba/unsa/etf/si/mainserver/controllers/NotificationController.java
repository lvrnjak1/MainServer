package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.*;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.products.AdminMerchantNotificationRepository;
import ba.unsa.etf.si.mainserver.requests.business.CloseOfficeRequest;
import ba.unsa.etf.si.mainserver.requests.business.NotificationRequest;
import ba.unsa.etf.si.mainserver.requests.business.OpenOfficeRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.business.CloseOfficeResponse;
import ba.unsa.etf.si.mainserver.responses.business.MANotificationResponse;
import ba.unsa.etf.si.mainserver.responses.business.NotificationResponse;
import ba.unsa.etf.si.mainserver.responses.business.OpenOfficeResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.NotificationService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    private  final NotificationService notificationService;
    private final BusinessService businessService;
    private final EmployeeProfileService employeeProfileService;
    private final AdminMerchantNotificationRepository adminMerchantNotificationRepository;
    private final OfficeService officeService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, BusinessService businessService,
                                  EmployeeProfileService employeeProfileService, AdminMerchantNotificationRepository adminMerchantNotificationRepository, OfficeService officeService, UserService userService) {
        this.notificationService = notificationService;
        this.businessService = businessService;
        this.employeeProfileService = employeeProfileService;
        this.adminMerchantNotificationRepository = adminMerchantNotificationRepository;
        this.officeService = officeService;
        this.userService = userService;
    }

    @PostMapping("/send")
    @Secured({"ROLE_MANAGER"})
    public NotificationResponse addNewNotification(@CurrentUser UserPrincipal userPrincipal,
                                                   @RequestBody NotificationRequest notificationRequest){
        User user = userService.findUserById(notificationRequest.getEmployeeId());
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Notification notification = null;
        try {
            notification = new Notification(business, employeeProfile,
                    notificationRequest.isHired(), notificationRequest.getDateFromString(),
                    notificationRequest.getTimeFromString());
        } catch (ParseException e) {
        }
        return new NotificationResponse(notificationService.save(notification));

    }

    @GetMapping("/read")
    @Secured({"ROLE_MERCHANT","ROLE_MANAGER"})
    public List<NotificationResponse> getAllReadNotifications(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return notificationService.findAllByBusinessId(business.getId())
                .stream()
                .filter(notification -> notification.isRead())
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/unread")
    @Secured({"ROLE_MERCHANT","ROLE_MANAGER"})
    public List<NotificationResponse> getAllUnreadNotifications(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return notificationService.findAllByBusinessId(business.getId())
                .stream()
                .filter(notification -> !notification.isRead())
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/{notificationId}/markRead")
    @Secured({"ROLE_MERCHANT"})
    public NotificationResponse markNotification(@CurrentUser UserPrincipal userPrincipal,
                                                 @PathVariable Long notificationId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Notification notification = notificationService.findByIdInBusiness(notificationId, business);
        boolean read = notification.isRead();
        notification.setRead(!read);
        return new NotificationResponse(notificationService.save(notification));
    }

    @DeleteMapping("/{notificationId}")
    @Secured({"ROLE_MERCHANT"})
    public ApiResponse deleteNotification(@CurrentUser UserPrincipal userPrincipal,
                                                   @PathVariable Long notificationId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Notification notification = notificationService.findByIdInBusiness(notificationId, business);
        notificationService.delete(notification);
        return new ApiResponse("Notification successfully deleted", 200);
    }

    @PostMapping("/office/open")
    @Secured("ROLE_MERCHANT")
    public ResponseEntity<ApiResponse> notifyAdminToOpen(@CurrentUser UserPrincipal userPrincipal,
                                                         @RequestBody OpenOfficeRequest notificationRequest) throws ParseException {
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);

        AdminMerchantNotification adminMerchantNotification = new AdminMerchantNotification(
                business,
                notificationRequest.getAddress(),
                notificationRequest.getCity(),
                notificationRequest.getCountry(),
                notificationRequest.getEmail(),
                notificationRequest.getPhoneNumber(),
                notificationRequest.getWorkDayStartDateFromString(),
                notificationRequest.getWorkDayEndDateFromString(),
                true
        );
        adminMerchantNotificationRepository.save(adminMerchantNotification);
        return ResponseEntity.ok(new ApiResponse("Notification successfully sent", 200));
    }

    @PostMapping("/office/close")
    @Secured("ROLE_MERCHANT")
    public ResponseEntity<ApiResponse> notifyAdminToClose(@CurrentUser UserPrincipal userPrincipal,
                                                          @RequestBody CloseOfficeRequest notificationRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Office office = officeService.findOfficeById(notificationRequest.getOfficeId(), business.getId());

        AdminMerchantNotification adminMerchantNotification = new AdminMerchantNotification(
                business,
                office.getContactInformation().getAddress(),
                office.getContactInformation().getCity(),
                office.getContactInformation().getCountry(),
                office.getContactInformation().getEmail(),
                office.getContactInformation().getPhoneNumber(),
                office.getWorkDayStart(),
                office.getWorkDayEnd(),
                false
        );
        adminMerchantNotification.setOfficeId(office.getId());
        adminMerchantNotificationRepository.save(adminMerchantNotification);
        return ResponseEntity.ok(new ApiResponse("Notification successfully sent", 200));
    }

    @GetMapping("/admin")
    @Secured("ROLE_ADMIN")
    public List<MANotificationResponse> getAllNotifications(){
        return adminMerchantNotificationRepository
                .findAll()
                .stream()
                .map(notification -> {
                    if(notification.isOpen()){
                        return new OpenOfficeResponse(notification);
                    }
                    return new CloseOfficeResponse(notification);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/admin/unread")
    @Secured("ROLE_ADMIN")
    public List<MANotificationResponse> getAllUnReadNotifications(){
        return adminMerchantNotificationRepository
                .findAll()
                .stream()
                .filter(notification -> !notification.isRead())
                .map(notification -> {
                    if(notification.isOpen()){
                        return new OpenOfficeResponse(notification);
                    }
                    return new CloseOfficeResponse(notification);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/admin/read")
    @Secured("ROLE_ADMIN")
    public List<MANotificationResponse> getAllReadNotifications(){
        return adminMerchantNotificationRepository
                .findAll()
                .stream()
                .filter(AdminMerchantNotification::isRead)
                .map(notification -> {
                    if(notification.isOpen()){
                        return new OpenOfficeResponse(notification);
                    }
                    return new CloseOfficeResponse(notification);
                })
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
