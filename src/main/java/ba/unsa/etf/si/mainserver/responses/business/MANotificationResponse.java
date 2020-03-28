package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.AdminMerchantNotification;
import ba.unsa.etf.si.mainserver.requests.business.OfficeRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MANotificationResponse {
    private Long businessId;
    private boolean open;
    private OfficeRequest office;
    private boolean read;

    public MANotificationResponse(AdminMerchantNotification adminMerchantNotification) {
        this.businessId = adminMerchantNotification.getBusiness().getId();
        this.open = adminMerchantNotification.isOpen();
        this.office = new OfficeRequest(adminMerchantNotification.getOffice().getAddress(),
                adminMerchantNotification.getOffice().getCity(),
                adminMerchantNotification.getOffice().getCountry(),
                adminMerchantNotification.getOffice().getEmail(),
                adminMerchantNotification.getOffice().getPhoneNumber());
        this.read = adminMerchantNotification.isRead();
    }
}
