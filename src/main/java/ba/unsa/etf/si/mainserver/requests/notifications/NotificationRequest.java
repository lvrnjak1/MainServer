package ba.unsa.etf.si.mainserver.requests.notifications;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String type;
    private NotificationPayload payload;
}
