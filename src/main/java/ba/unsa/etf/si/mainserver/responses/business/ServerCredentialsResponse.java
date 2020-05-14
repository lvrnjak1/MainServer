package ba.unsa.etf.si.mainserver.responses.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerCredentialsResponse {
    private Long userId;
    private String username;
    private String password;
}
