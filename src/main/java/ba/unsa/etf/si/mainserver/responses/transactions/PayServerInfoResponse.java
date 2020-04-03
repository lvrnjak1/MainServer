package ba.unsa.etf.si.mainserver.responses.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.Receipt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayServerInfoResponse {
    private String receiptId;
    private String service;
    private BigDecimal totalPrice;

    public PayServerInfoResponse(Receipt receipt){
        this.receiptId = receipt.getReceiptId();
        this.totalPrice = receipt.getTotalPrice();
        this.service = receipt.getReceiptItems()
                .stream()
                .map(receiptItem -> receiptItem.getProductName() + " (" + receiptItem.getQuantity() + ")")
                .collect(Collectors.joining(","));
    }
}
