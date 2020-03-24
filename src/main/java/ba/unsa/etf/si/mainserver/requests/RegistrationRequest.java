package ba.unsa.etf.si.mainserver.requests;

import ba.unsa.etf.si.mainserver.responses.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;
    private List<RoleResponse> roles;
}
