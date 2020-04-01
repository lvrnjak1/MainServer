package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileResponse {
    private Long id;
    private String name;
    private String surname;
    private Date dateOfBirth;
    private String jmbg;
    private String address;
    private String city;
    private String country;
    private String email;
    private String phoneNumber;

    public EmployeeProfileResponse(EmployeeProfile employeeProfile) {
        this.id = employeeProfile.getId();
        this.name = employeeProfile.getName();
        this.surname = employeeProfile.getSurname();
        this.address = employeeProfile.getContactInformation().getAddress();
        this.city = employeeProfile.getContactInformation().getCity();
        this.country = employeeProfile.getContactInformation().getCountry();
        this.email = employeeProfile.getContactInformation().getEmail();
        this.phoneNumber = employeeProfile.getContactInformation().getPhoneNumber();
        this.dateOfBirth = employeeProfile.getDateOfBirth();
        this.jmbg = employeeProfile.getJmbg();
    }

}
