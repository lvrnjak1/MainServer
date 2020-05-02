package ba.unsa.etf.si.mainserver.requests.merchant_dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeInventoryRequest {
    private Long officeId;
    private ArrayList<ProductQuantityData> products;
}
