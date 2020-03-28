package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.Notification;

import java.time.LocalDateTime;
import java.util.Date;

public class NotificationResponse {
    private Long id;
    private EmployeeProfileResponse employee;
    private boolean hired;
    private boolean read;
    private LocalDateTime date;

    public NotificationResponse(Notification notification){
        this.id = notification.getId();
        this.employee = new EmployeeProfileResponse(notification.getEmployeeProfile());
        this.hired = notification.isHired();
        this.read = notification.isRead();
        this.date = notification.getDate();
    }
}
