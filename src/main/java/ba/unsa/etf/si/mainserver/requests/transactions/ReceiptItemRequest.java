package ba.unsa.etf.si.mainserver.requests.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemRequest {
    @NotBlank
    private Long id; //productId koji dobijem od cash servera bit ce isti kao moj product id
    @NotBlank
    private Long quantity;
}
