package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResponse {
    private int percentage = 0;

    public DiscountResponse(Discount discount){
        if(discount != null) {
            this.percentage = discount.getPercentage();
        }
    }
}
