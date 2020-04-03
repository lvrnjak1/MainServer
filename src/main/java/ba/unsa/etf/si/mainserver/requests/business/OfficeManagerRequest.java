package ba.unsa.etf.si.mainserver.requests.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeManagerRequest {
    @NotBlank
    private Long userId;
    @NotBlank
    private String email;
    @NotBlank
    private String username;
    @NotBlank
    private Long employeeId;
}
