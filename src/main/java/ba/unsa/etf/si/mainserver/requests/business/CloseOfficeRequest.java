package ba.unsa.etf.si.mainserver.requests.business;

import ba.unsa.etf.si.mainserver.models.business.AdminMerchantNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseOfficeRequest {
    @NotBlank
    private Long officeId;
    public CloseOfficeRequest(AdminMerchantNotification adminMerchantNotification){
        this.officeId = adminMerchantNotification.getOfficeId();
    }
}
