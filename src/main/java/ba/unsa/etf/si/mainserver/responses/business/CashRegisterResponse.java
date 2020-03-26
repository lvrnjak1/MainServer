package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashRegisterResponse {
    private Long id;

    public CashRegisterResponse(CashRegister cashRegister){
        this.id = cashRegister.getId();
    }
}
