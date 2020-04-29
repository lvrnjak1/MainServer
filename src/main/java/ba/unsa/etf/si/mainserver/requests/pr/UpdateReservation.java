package ba.unsa.etf.si.mainserver.requests.pr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservation {
    private String email;
    private Long verificationCode;
}
