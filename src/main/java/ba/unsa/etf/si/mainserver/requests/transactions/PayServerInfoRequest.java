package ba.unsa.etf.si.mainserver.requests.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayServerInfoRequest {
    @NotBlank
    private Long cashRegisterId;
    @NotBlank
    private Long officeId;
    @NotBlank
    private String businessName;
}
