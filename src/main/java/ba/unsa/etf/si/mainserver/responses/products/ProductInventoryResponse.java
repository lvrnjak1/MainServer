package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.items.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryResponse {
    private ProductResponse product;
    private double quantity;

    public ProductInventoryResponse(Product product, OfficeInventory officeInventory, List<Item> productItems){
        this.product = new ProductResponse(product, productItems);
        this.quantity = officeInventory.getQuantity();
    }
}
