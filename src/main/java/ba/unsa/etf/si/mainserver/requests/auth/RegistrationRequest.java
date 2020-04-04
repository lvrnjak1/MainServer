package ba.unsa.etf.si.mainserver.requests.auth;

import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotNull
    private List<RoleResponse> roles;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String dateOfBirth;
    @NotBlank
    private String jmbg;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
    @NotBlank
    private String phoneNumber;
    private Long businessId;

    public Date getDateFromString() throws ParseException {
        return dateOfBirth == null ? null : new SimpleDateFormat("dd.MM.yyyy").parse(dateOfBirth);
    }
}
