package ba.unsa.etf.si.mainserver.requests.business;

import ba.unsa.etf.si.mainserver.models.business.AdminMerchantNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseOfficeRequest {
    private Long officeId;
    public CloseOfficeRequest(AdminMerchantNotification adminMerchantNotification){
        this.officeId = adminMerchantNotification.getOfficeId();
    }
}
