package ba.unsa.etf.si.mainserver.responses.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.Receipt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponse {
    private String receiptId;
    private Long cashRegisterId;
    private Long officeId;
    private String username;
    private BigDecimal totalPrice;
    private Long timestamp;
    private String status;
    private List<ReceiptItemResponse> receiptItems;

    public ReceiptResponse(Receipt receipt){
        this.receiptId = receipt.getReceiptId();
        this.cashRegisterId = receipt.getCashRegisterId();
        this.officeId = receipt.getOfficeId();
        this.username = receipt.getUsername();
        this.totalPrice = receipt.getTotalPrice();
        this.timestamp = receipt.getTimestamp().getTime();
        this.status = receipt.getStatus().toString();
        //this.receiptItems = receipt.getReceiptItems().stream().map(ReceiptItemResponse::new).collect(Collectors.toList());
    }
}
