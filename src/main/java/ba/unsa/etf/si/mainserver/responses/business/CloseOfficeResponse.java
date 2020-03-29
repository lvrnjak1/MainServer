package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.AdminMerchantNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseOfficeResponse extends MANotificationResponse {
    private Long officeId;

    public CloseOfficeResponse(AdminMerchantNotification adminMerchantNotification){
        super(adminMerchantNotification);
        this.officeId = adminMerchantNotification.getOfficeId();
    }
}
