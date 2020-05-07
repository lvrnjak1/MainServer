package ba.unsa.etf.si.mainserver.responses;

import ba.unsa.etf.si.mainserver.models.PDV;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDVResponse {
    private double pdv;
    private boolean active;

    public PDVResponse(PDV pdv){
        this.pdv = pdv.getPdvRate();
        this.active = pdv.isActive();
    }
}
