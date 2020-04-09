package ba.unsa.etf.si.mainserver.models.pr;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.business.Office;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "office_reviews")
public class OfficeReview extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private int starReview;
    private String text;
    private int likes;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "office_id")
    private Office office;

    public OfficeReview(String firstName, String lastName, String email, int starReview, String text, int likes,
                        Office office){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.starReview = starReview;
        this.text = text;
        this.likes = likes;
        this.office = office;
    }
}
