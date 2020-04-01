package ba.unsa.etf.si.mainserver.requests.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileRequest {
    private String name;
    private String surname;
    private String dateOfBirth;
    private String jmbg;
    private String address;
    private String city;
    private String country;
    private String phoneNumber;

    public Date getDateFromString() throws ParseException {
        return dateOfBirth == null ? null : new SimpleDateFormat("dd.MM.yyyy").parse(dateOfBirth);
    }
}
