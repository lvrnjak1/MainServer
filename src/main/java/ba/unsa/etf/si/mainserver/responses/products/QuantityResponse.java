package ba.unsa.etf.si.mainserver.responses.products;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityResponse {
    private Long productId;
    private double quantity;
}
