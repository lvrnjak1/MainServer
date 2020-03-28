package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResponse {
    private ProductResponse product;
    private double quantity;

    public WarehouseResponse(Warehouse warehouse){
        this.product = new ProductResponse(warehouse.getProduct());
        this.quantity = warehouse.getQuantity();
    }
}
