package ba.unsa.etf.si.mainserver.requests.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileRequest {
    private String name;
    private String surname;
    private String address;
    private String city;
    private String country;
    private String phoneNumber;
}
