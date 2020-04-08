package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashRegisterWithUUIDResponse {
    private Long id;
    private String name;
    private String uuid;

    public CashRegisterWithUUIDResponse(CashRegister cashRegister){
        this.id = cashRegister.getId();
        this.name = cashRegister.getName();
        this.uuid = cashRegister.getUuid();
    }
}
