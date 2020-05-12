package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.items.ProductItem;
import ba.unsa.etf.si.mainserver.responses.products.items.ItemTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtendedProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private double pdv;
    private String image = null;
    private String unit;
    private DiscountResponse discount;
    private String barcode;
    private String business;
    private String description;
    private ItemTypeResponse itemType;
    private List<ProductItemResponse> items;

    public ExtendedProductResponse(Product product){
        setAttributes(product);
    }

    public ExtendedProductResponse(Product product, List<ProductItem> items) {
        setAttributes(product);
        this.items = items.stream().map(ProductItemResponse::new).collect(Collectors.toList());
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
        this.business = product.getBusiness().getName();
        this.barcode = product.getBarcode();
        this.description = product.getDescription();
        this.pdv = product.getPdv();
        if(product.getItemType()!= null) {
            this.itemType = new ItemTypeResponse(product.getItemType());
        }
    }
}
