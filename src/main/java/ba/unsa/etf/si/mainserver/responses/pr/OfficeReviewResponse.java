package ba.unsa.etf.si.mainserver.responses.pr;

import ba.unsa.etf.si.mainserver.models.pr.OfficeReview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeReviewResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String text;
    private int starReview;
    private int likes;
    private OfficeResponseLite office;

    public OfficeReviewResponse(OfficeReview officeReview){
        this.id = officeReview.getId();
        this.firstName = officeReview.getFirstName();
        this.lastName = officeReview.getLastName();
        this.email = officeReview.getEmail();
        this.text = officeReview.getText();
        this.starReview = officeReview.getStarReview();
        this.likes = officeReview.getLikes();
        this.office = new OfficeResponseLite(officeReview.getOffice());
    }
}
