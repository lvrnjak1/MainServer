package ba.unsa.etf.si.mainserver.responses;

import ba.unsa.etf.si.mainserver.responses.business.CashRegisterWithUUIDResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashServerConfigResponse {
    private String businessName;
    private boolean restaurant;
    private List<CashRegisterWithUUIDResponse> cashRegisters;
    private String language;
    private String syncTime;
    private String startTime;
    private String endTime;
    private String placeName;
    private Long businessId;
    private Long officeId;
}
