package ba.unsa.etf.si.mainserver.responses;

import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String dateOfBirth;
    private String jmbg;
    private String address;
    private String phoneNumber;
    private String country;
    private String city;
    private List<RoleResponse> roles;

    public UserResponse(Long userId, String username, String email, String name, String surname, String dateOfBirth,
                        String jmbg, String address, String phoneNumber, String country, String city, User user) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.jmbg = jmbg;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;

        setRoles(user);
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
