package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.business.Notification;
import ba.unsa.etf.si.mainserver.requests.business.NotificationRequest;
import ba.unsa.etf.si.mainserver.responses.business.NotificationResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.NotificationService;

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

    public NotificationController(NotificationService notificationService, BusinessService businessService,
                                  EmployeeProfileService employeeProfileService) {
        this.notificationService = notificationService;
        this.businessService = businessService;
        this.employeeProfileService = employeeProfileService;
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
                        notificationRequest.isHired(), notificationRequest.isRead(),
                        notificationRequest.getDateFromString(),notificationRequest.getTimeFromString());
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
}
