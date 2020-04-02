package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.InventoryLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryLogResponse {
    private String productName;
    private Long officeId;
    private double quantity;
    private String date;
    private String time;

    public InventoryLogResponse(InventoryLog inventoryLog){
        this.productName = inventoryLog.getProduct().getName();
        this.officeId = inventoryLog.getOffice().getId();
        this.quantity = inventoryLog.getQuantity();
        this.date = inventoryLog.getStringDate();
        this.time = inventoryLog.getStringTime();
    }
}
