package ba.unsa.etf.si.mainserver.requests.products.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemRequest {
    private Long productId;
    private Long itemId;
    private double value;
}
