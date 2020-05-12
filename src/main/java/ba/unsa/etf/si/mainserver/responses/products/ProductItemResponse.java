package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.items.Item;
import ba.unsa.etf.si.mainserver.models.products.items.ProductItem;
import ba.unsa.etf.si.mainserver.responses.products.items.ItemResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemResponse {
    private ItemResponse item;
    private double value;

    public ProductItemResponse(ProductItem productItem){
        this.item = new ItemResponse(productItem.getItem());
        this.value = productItem.getValue();
    }
}
