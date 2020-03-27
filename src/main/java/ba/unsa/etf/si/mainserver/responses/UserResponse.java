package ba.unsa.etf.si.mainserver.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String address;
    private String phoneNumber;
    private String country;
    private String city;
}
