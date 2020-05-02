package ba.unsa.etf.si.mainserver.requests.merchant_dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQuantityData {
    private Long id;
    private Long quantity;
}
