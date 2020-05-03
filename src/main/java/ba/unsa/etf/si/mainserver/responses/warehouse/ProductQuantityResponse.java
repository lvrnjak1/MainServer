package ba.unsa.etf.si.mainserver.responses.warehouse;

import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQuantityResponse {
    private ProductResponse product;
    private Long quantity;
    private double availableQuantity;
}
