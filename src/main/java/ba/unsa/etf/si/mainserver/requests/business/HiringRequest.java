package ba.unsa.etf.si.mainserver.requests.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HiringRequest {
    @NotBlank
    private Long employeeId;
    @NotBlank
    private Long officeId;
    @NotBlank
    private boolean cashier;
}
