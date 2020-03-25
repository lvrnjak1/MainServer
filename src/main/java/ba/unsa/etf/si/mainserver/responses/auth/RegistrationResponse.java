package ba.unsa.etf.si.mainserver.responses.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private Long id;
    private String username;
    private String password;
    private String email;
    private List<RoleResponse> roles;
}
