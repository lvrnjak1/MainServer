package ba.unsa.etf.si.mainserver.responses.transactions;

import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItemResponse {
    private ProductResponse product;
    private double quantity;

    public ReceiptItemResponse(Product product, double quantity){
        this.product = new ProductResponse(product);
        this.quantity = quantity;
    }
}
