package ba.unsa.etf.si.mainserver.requests.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveNewPasswordRequest {
    private String token;
    private String newPassword;
}