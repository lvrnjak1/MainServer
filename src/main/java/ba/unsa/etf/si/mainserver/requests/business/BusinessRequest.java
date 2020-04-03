package ba.unsa.etf.si.mainserver.requests.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessRequest {
    @NotBlank
    private String name;
    @NotBlank
    private boolean restaurantFeature = false;
    private Long merchantId;
}
