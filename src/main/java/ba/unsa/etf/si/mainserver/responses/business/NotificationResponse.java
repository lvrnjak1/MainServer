package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private EmployeeProfileResponse employee;
    private boolean hired;
    private boolean read;
    private String date;
    private String time;

    public NotificationResponse(Notification notification){
        this.id = notification.getId();
        this.employee = new EmployeeProfileResponse(notification.getEmployeeProfile());
        this.hired = notification.isHired();
        this.read = notification.isRead();
        this.date = notification.getStringDate();
        this.time = notification.getStringTime();
    }
}
