package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeInventoryResponse {
    private Long productId;
    private Long officeId;
    private double quantity;

    public OfficeInventoryResponse(OfficeInventory officeInventory) {
        this.productId = officeInventory.getProduct().getId();
        this.officeId = officeInventory.getOffice().getId();
        this.quantity = officeInventory.getQuantity();
    }
}
