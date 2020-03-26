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

    public EmployeeProfileResponse(EmployeeProfile employeeProfile) {
        this.id = employeeProfile.getId();
        this.name = employeeProfile.getName();
        this.surname = employeeProfile.getSurname();
    }

    //dodati ostalo
}
