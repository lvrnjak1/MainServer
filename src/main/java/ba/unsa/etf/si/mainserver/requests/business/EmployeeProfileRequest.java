package ba.unsa.etf.si.mainserver.requests.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileRequest {
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

    public Date getDateFromString() throws ParseException {
        return dateOfBirth == null ? null : new SimpleDateFormat("dd.MM.yyyy").parse(dateOfBirth);
    }
}
