package ba.unsa.etf.si.mainserver.responses.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.ReceiptItem;
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

    public ReceiptItemResponse(ReceiptItem receiptItem){
        this.product = new ProductResponse(receiptItem.getProduct());
        this.quantity = receiptItem.getQuantity();
    }
}
