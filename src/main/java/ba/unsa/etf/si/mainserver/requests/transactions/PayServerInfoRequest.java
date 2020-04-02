package ba.unsa.etf.si.mainserver.requests.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayServerInfoRequest {
    private Long cashRegisterId;
    private Long officeId;
    private String businessName;
}
