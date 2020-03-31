package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
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

    public NotificationController(NotificationService notificationService, BusinessService businessService,
                                  EmployeeProfileService employeeProfileService, AdminMerchantNotificationRepository adminMerchantNotificationRepository, OfficeService officeService) {
        this.notificationService = notificationService;
        this.businessService = businessService;
        this.employeeProfileService = employeeProfileService;
        this.adminMerchantNotificationRepository = adminMerchantNotificationRepository;
        this.officeService = officeService;
    }

    @PostMapping("/send")
    @Secured({"ROLE_MANAGER"})
    public NotificationResponse addNewNotification(@CurrentUser UserPrincipal userPrincipal,
                                                   @RequestBody NotificationRequest notificationRequest){
        Optional<EmployeeProfile> employeeProfileOptional = employeeProfileService.findById(notificationRequest.getEmployeeId());
        if(employeeProfileOptional.isPresent()) {
            Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
            Notification notification = null;
            try {
                notification = new Notification(business, employeeProfileOptional.get(),
                        notificationRequest.isHired(), notificationRequest.getDateFromString(),
                        notificationRequest.getTimeFromString());
            } catch (ParseException e) {
            }
            return new NotificationResponse(notificationService.save(notification));

        }
        throw new AppException("Employee with id " + notificationRequest.getEmployeeId() + " doesn't exist");
    }

    @GetMapping("/read")
    @Secured({"ROLE_MERCHANT","ROLE_MANAGER"})
    public List<NotificationResponse> getAllReadNotifications(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        System.out.println(notificationService.findAllByBusinessId(business.getId()));
        return notificationService.findAllByBusinessId(business.getId())
                .stream()
                .filter(notification -> notification.isRead())
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/unread")
    @Secured({"ROLE_MERCHANT","ROLE_MANAGER"})
    public List<NotificationResponse> getAllUnreadNotifications(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
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
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Notification notification = notificationService.findByIdInBusiness(notificationId, business);
        boolean read = notification.isRead();
        notification.setRead(!read);
        return new NotificationResponse(notificationService.save(notification));
    }

    @PostMapping("/office/open")
    @Secured("ROLE_MERCHANT")
    public ResponseEntity<ApiResponse> notifyAdminToOpen(@CurrentUser UserPrincipal userPrincipal,
                                                         @RequestBody OpenOfficeRequest notificationRequest){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        AdminMerchantNotification adminMerchantNotification = new AdminMerchantNotification(
                business,
                notificationRequest.getAddress(),
                notificationRequest.getCity(),
                notificationRequest.getCountry(),
                notificationRequest.getEmail(),
                notificationRequest.getPhoneNumber(),
                true
        );
        adminMerchantNotificationRepository.save(adminMerchantNotification);
        return ResponseEntity.ok(new ApiResponse("Notification successfully sent", 200));
    }

    @PostMapping("/office/close")
    @Secured("ROLE_MERCHANT")
    public ResponseEntity<ApiResponse> notifyAdminToClose(@CurrentUser UserPrincipal userPrincipal,
                                                          @RequestBody CloseOfficeRequest notificationRequest){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Office> officeOptional = officeService.findById(notificationRequest.getOfficeId());
        if(!officeOptional.isPresent()){
            throw new ResourceNotFoundException("Office with this id doesn't exist");
        }

        AdminMerchantNotification adminMerchantNotification = new AdminMerchantNotification(
                business,
                officeOptional.get().getContactInformation().getAddress(),
                officeOptional.get().getContactInformation().getCity(),
                officeOptional.get().getContactInformation().getCountry(),
                officeOptional.get().getContactInformation().getEmail(),
                officeOptional.get().getContactInformation().getPhoneNumber(),
                false
        );
        adminMerchantNotification.setOfficeId(officeOptional.get().getId());
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
