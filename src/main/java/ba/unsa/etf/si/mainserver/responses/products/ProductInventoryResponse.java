package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import ba.unsa.etf.si.mainserver.models.products.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryResponse {
    private ProductResponse product;
    private double quantity;

    public ProductInventoryResponse(Product product, OfficeInventory officeInventory){
        this.product = new ProductResponse(product);
        this.quantity = officeInventory.getQuantity();
    }
}
