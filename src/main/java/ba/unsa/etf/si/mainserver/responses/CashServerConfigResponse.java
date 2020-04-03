package ba.unsa.etf.si.mainserver.responses;

import ba.unsa.etf.si.mainserver.responses.business.CashRegisterResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashServerConfigResponse {
    private String businessName;
    private List<CashRegisterResponse> cashRegisters;
}
