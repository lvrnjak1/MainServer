package ba.unsa.etf.si.mainserver.requests.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MANotificationRequest {
    private String address;
    private String city;
    private String country;
    private String email;
    private String phoneNumber;
    private boolean open;

}
