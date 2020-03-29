package ba.unsa.etf.si.mainserver.responses.auth;

import ba.unsa.etf.si.mainserver.models.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordResponse {
    private Long id;
    private String username;
    private String password;
    private String email;

    public ResetPasswordResponse(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
    }
}


