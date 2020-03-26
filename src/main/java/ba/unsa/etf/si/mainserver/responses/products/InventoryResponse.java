package ba.unsa.etf.si.mainserver.responses.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private ProductResponse productResponse;
    //private OfficeResponse officeResponse;
    private double quantity;
}
