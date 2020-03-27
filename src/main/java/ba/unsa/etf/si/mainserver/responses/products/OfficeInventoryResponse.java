package ba.unsa.etf.si.mainserver.responses.products;

import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import ba.unsa.etf.si.mainserver.responses.business.CashRegisterResponse;
import ba.unsa.etf.si.mainserver.responses.business.OfficeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeInventoryResponse {
    private ProductResponse product;
    private OfficeResponse office;
    private double quantity;

    public OfficeInventoryResponse(OfficeInventory officeInventory, List<CashRegisterResponse> cashRegisterResponseList) {
        this.product = new ProductResponse(officeInventory.getProduct());
        this.office = new OfficeResponse(officeInventory.getOffice(), cashRegisterResponseList);
        this.quantity = officeInventory.getQuantity();
    }
}
