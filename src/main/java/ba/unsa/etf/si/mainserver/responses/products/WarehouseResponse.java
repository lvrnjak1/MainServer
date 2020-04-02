package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResponse {
    private Long  productId;
    private String productName;
    private double quantity;

    public WarehouseResponse(Warehouse warehouse){
        this.productId = warehouse.getProduct().getId();
        this.productName = warehouse.getProduct().getName();
        this.quantity = warehouse.getQuantity();
    }
}
