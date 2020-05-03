package ba.unsa.etf.si.mainserver.responses.warehouse;

import ba.unsa.etf.si.mainserver.responses.business.OfficeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeInventoryRequestResponse {
    private Long requestId;
    private OfficeResponse office;
    private ArrayList<ProductQuantityResponse> requests;
}
