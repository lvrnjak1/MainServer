package ba.unsa.etf.si.mainserver.requests.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest {
    @NotBlank
    private int percentage;
}
