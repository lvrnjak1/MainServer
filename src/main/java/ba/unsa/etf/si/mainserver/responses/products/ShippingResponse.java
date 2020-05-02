package ba.unsa.etf.si.mainserver.responses.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingResponse {
    private Long productId;
    private String productName;
    private double quantity;
}
