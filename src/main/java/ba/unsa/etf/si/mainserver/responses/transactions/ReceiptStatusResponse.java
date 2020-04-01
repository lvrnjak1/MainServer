package ba.unsa.etf.si.mainserver.responses.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.ReceiptStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptStatusResponse {
    private String receiptId;
    private String status;

    public ReceiptStatusResponse(ReceiptStatus receiptStatus, String receiptId){
        this.receiptId = receiptId;
        this.status = receiptStatus.getStatusName().toString();
    }
}
