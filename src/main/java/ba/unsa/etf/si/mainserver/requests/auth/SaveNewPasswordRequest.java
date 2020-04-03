package ba.unsa.etf.si.mainserver.requests.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveNewPasswordRequest {
    @NotBlank
    private String token;
    @NotBlank
    private String newPassword;
}
