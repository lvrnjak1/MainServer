package ba.unsa.etf.si.mainserver.requests.pr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeReviewRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String text;
    private int starReview;
}
