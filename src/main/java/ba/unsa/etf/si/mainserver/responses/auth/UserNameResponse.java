package ba.unsa.etf.si.mainserver.responses.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNameResponse {
    private String username;
    private String name;
    private String surname;
}
