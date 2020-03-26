package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.Business;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessResponse {
    private Long id;
    private String name;
    private boolean restaurantFeature;
    private Set<OfficeResponse> officeResponses;

    public BusinessResponse(Business business){
        this.id = business.getId();
        this.name = business.getName();
        this.restaurantFeature = business.isRestaurantFeature();
        this.officeResponses = business.getOffices().stream()
                .map(OfficeResponse::new).collect(Collectors.toSet());
    }
}
