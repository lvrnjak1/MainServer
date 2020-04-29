package ba.unsa.etf.si.mainserver.requests.notifications;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload {
    private String subject;
    private String action;
    private String description;
}
