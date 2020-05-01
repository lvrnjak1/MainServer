package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.Business;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessResponse {
    private Long id;
    private String name;
    private boolean restaurantFeature;
    private String syncTime;
    private List<OfficeResponse> offices;
    private EmployeeProfileResponse merchant;

    public BusinessResponse(Business business, List<OfficeResponse> offices){
        this.id = business.getId();
        this.name = business.getName();
        this.restaurantFeature = business.isRestaurantFeature();
        this.syncTime = business.getStringSyncDate();
        this.offices = offices;
        this.merchant = new EmployeeProfileResponse(business.getMerchant());
    }
}
