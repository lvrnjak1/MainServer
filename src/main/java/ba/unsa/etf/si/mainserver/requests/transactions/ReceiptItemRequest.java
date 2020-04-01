package ba.unsa.etf.si.mainserver.requests.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemRequest {
    private Long id; //productId koji dobijem od cash servera bit ce isti kao moj product id
    private Long quantity;
}
