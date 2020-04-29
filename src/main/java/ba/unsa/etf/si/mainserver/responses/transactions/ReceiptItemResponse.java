package ba.unsa.etf.si.mainserver.responses.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.ReceiptItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItemResponse {
    //private ProductResponse product;
    private String productName;
    private String barcode;
    private BigDecimal price;
    private double pdv;
    private int discountPercentage;
    private String unit;
    private double quantity;

    public ReceiptItemResponse(ReceiptItem receiptItem){
        //this.product = new ProductResponse(receiptItem.getProduct());
        this.productName = receiptItem.getProductName();
        this.barcode = receiptItem.getBarcode();
        this.price = receiptItem.getPrice();
        this.discountPercentage = receiptItem.getDiscountPercentage();
        this.unit = receiptItem.getUnit();
        this.quantity = receiptItem.getQuantity();
        this.pdv = receiptItem.getPdv();
    }
}
