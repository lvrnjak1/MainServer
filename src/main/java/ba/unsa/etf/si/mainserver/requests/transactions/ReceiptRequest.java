package ba.unsa.etf.si.mainserver.requests.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptRequest {
    private String receiptId;
    private Long cashRegisterId;
    private Long officeId;
    private Long businessId;
    private String username;
    private BigDecimal totalPrice;
    private Long timestamp;
    private List<ReceiptItemRequest> receiptItems;
}
