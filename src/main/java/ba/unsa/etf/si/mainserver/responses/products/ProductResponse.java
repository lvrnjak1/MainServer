package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.items.Item;
import ba.unsa.etf.si.mainserver.responses.products.items.ItemResponse;
import ba.unsa.etf.si.mainserver.responses.products.items.ItemTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private double pdv;
    private String unit;
    private String barcode;
    private String description;
    private DiscountResponse discount;
    private ItemTypeResponse itemType;
    private List<ItemResponse> productItems = null;
    private String image = null;

    public ProductResponse(Product product){
        setAttributes(product);
    }

    public ProductResponse(Product product, List<Item> productItems){
        setAttributes(product);
        this.productItems = productItems.stream().map(ItemResponse::new).collect(Collectors.toList());
    }

    private void setAttributes(Product product){
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
        this.description = product.getDescription();
        this.pdv = product.getPdv();
        if(product.getItemType() != null) {
            this.itemType = new ItemTypeResponse(product.getItemType());
        }
    }
}
