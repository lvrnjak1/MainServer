package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessResponse {
    private Long id;
    private String name;
    private boolean restaurantFeature;
    private List<OfficeResponse> offices;

    public BusinessResponse(Business business, List<OfficeResponse> offices){
        this.id = business.getId();
        this.name = business.getName();
        this.restaurantFeature = business.isRestaurantFeature();
        this.offices = offices;
    }
}
