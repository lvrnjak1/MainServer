package ba.unsa.etf.si.mainserver.requests.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseRequest {
    @NotBlank
    private Long productId;
    @NotBlank
    private double quantity;
}
