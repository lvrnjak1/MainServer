package ba.unsa.etf.si.mainserver.requests.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptRequest {
    @NotBlank
    private String receiptId;
    @NotBlank
    private Long cashRegisterId;
    @NotBlank
    private Long officeId;
    @NotBlank
    private Long businessId;
    @NotBlank
    private String username;
    @NotBlank
    private BigDecimal totalPrice;
    @NotBlank
    private String status;
    @NotBlank
    private String paymentMethod;
    @NotBlank
    private Long timestamp;
    @NotBlank
    private List<ReceiptItemRequest> receiptItems;
}
