package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.WarehouseLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLogResponse {
    private String productName;
    private double quantity;
    private String date;
    private String time;

    public WarehouseLogResponse(WarehouseLog warehouseLog){
        this.productName = warehouseLog.getProduct().getName();
        this.quantity = warehouseLog.getQuantity();
        this.date = warehouseLog.getStringDate();
        this.time = warehouseLog.getStringTime();
    }
}

