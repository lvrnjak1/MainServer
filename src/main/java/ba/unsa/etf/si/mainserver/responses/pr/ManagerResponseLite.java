package ba.unsa.etf.si.mainserver.responses.pr;

import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerResponseLite {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public ManagerResponseLite(EmployeeProfile manager){
        this.id = manager.getId();
        this.firstName = manager.getName();
        this.lastName = manager.getSurname();
        this.email = manager.getContactInformation().getEmail();
        this.phoneNumber = manager.getContactInformation().getPhoneNumber();
    }
}
