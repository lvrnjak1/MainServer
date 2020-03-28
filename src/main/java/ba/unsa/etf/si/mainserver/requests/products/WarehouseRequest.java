package ba.unsa.etf.si.mainserver.requests.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseRequest {
    private Long productId;
    private double quantity;
}
