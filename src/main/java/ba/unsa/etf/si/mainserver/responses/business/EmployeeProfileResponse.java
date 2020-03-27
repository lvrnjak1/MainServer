package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.EmployeeProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileResponse {
    private Long id;
    private String name;
    private String surname;
    private String address;
    private String city;
    private String country;
    private String email;
    private String phoneNumber;

    public EmployeeProfileResponse(EmployeeProfile employeeProfile) {
        this.id = employeeProfile.getId();
        this.name = employeeProfile.getName();
        this.surname = employeeProfile.getSurname();
    }

}
