package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String image = null;
    private String unit;
    private String barcode;
    private DiscountResponse discount;

    public ProductResponse(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        if(product.getImage() != null) {
            String encodedString = Base64.getEncoder().encodeToString(product.getImage());
            this.image = "data:image/png;base64," + encodedString;
        }
        if(product.getDiscount() != null) {
            this.discount = new DiscountResponse(product.getDiscount());
        }else{
            this.discount = new DiscountResponse(0);
        }
        this.unit = product.getUnit();
        this.barcode = product.getBarcode();
    }
}
