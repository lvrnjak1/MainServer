package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileResponse {
    private Long id;
    private String name;
    private String surname;
    private String dateOfBirth;
    private String jmbg;
    private String address;
    private String city;
    private String country;
    private String email;
    private String phoneNumber;
    private List<RoleResponse> roles;

    public EmployeeProfileResponse(EmployeeProfile employeeProfile) {
        this.id = employeeProfile.getId();
        this.name = employeeProfile.getName();
        this.surname = employeeProfile.getSurname();
        this.address = employeeProfile.getContactInformation().getAddress();
        this.city = employeeProfile.getContactInformation().getCity();
        this.country = employeeProfile.getContactInformation().getCountry();
        this.email = employeeProfile.getContactInformation().getEmail();
        this.phoneNumber = employeeProfile.getContactInformation().getPhoneNumber();
        this.dateOfBirth = employeeProfile.getStringDate();
        this.jmbg = employeeProfile.getJmbg();

        setRoles(employeeProfile.getAccount());
    }

    public EmployeeProfileResponse(Long id, String name, String surname, String dateOfBirth, String jmbg, String address,
                                   String city, String country, String email, String phoneNumber, User account) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.jmbg = jmbg;
        this.address = address;
        this.city = city;
        this.country = country;
        this.email = email;
        this.phoneNumber = phoneNumber;

        setRoles(account);
    }

    public void setRoles(User account){
        this.roles = account.getRoles().stream()
                .map(
                        role -> new RoleResponse(
                                role.getId(),
                                role.getName().toString())
                )
                .collect(Collectors.toList());
    }

}
