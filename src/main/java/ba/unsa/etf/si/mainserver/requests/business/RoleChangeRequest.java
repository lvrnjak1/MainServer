package ba.unsa.etf.si.mainserver.requests.business;

import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleChangeRequest {
    @NotBlank
    private List<RoleResponse> newRoles;
}
