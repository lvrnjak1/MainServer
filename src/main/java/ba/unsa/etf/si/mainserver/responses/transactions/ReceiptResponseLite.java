package ba.unsa.etf.si.mainserver.responses.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponseLite {
    private String receiptId;
    private Long cashRegisterId;
    private Long timestamp;
    private String username;
    private BigDecimal itemPrice;
    private BigDecimal totalPrice;
    private double quantity;
}
