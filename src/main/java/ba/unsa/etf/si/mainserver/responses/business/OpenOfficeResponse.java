package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.AdminMerchantNotification;
import ba.unsa.etf.si.mainserver.requests.business.OfficeRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenOfficeResponse extends MANotificationResponse{
    private OfficeRequest office;

    public OpenOfficeResponse(AdminMerchantNotification adminMerchantNotification){
        super(adminMerchantNotification);
        this.office = new OfficeRequest(adminMerchantNotification.getAddress(),
                adminMerchantNotification.getCity(),
                adminMerchantNotification.getCountry(),
                adminMerchantNotification.getEmail(),
                adminMerchantNotification.getPhoneNumber(),
                adminMerchantNotification.getStringStart(),
                adminMerchantNotification.getStringEnd(),
                "username",
                "password"
        );
    }
}
