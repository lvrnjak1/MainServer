package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.AdminMerchantNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MANotificationResponse {
    private Long id;
    private Long businessId;
    private boolean open;
    private boolean read;

    public MANotificationResponse(AdminMerchantNotification adminMerchantNotification) {
        this.id = adminMerchantNotification.getId();
        this.businessId = adminMerchantNotification.getBusiness().getId();
        this.open = adminMerchantNotification.isOpen();
        this.read = adminMerchantNotification.isRead();
    }
}
