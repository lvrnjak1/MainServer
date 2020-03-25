package ba.unsa.etf.si.mainserver.models.pr;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.auth.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    private String text;

    @OneToOne(mappedBy = "answer")
    private Question question;

    @ManyToOne
    @JsonManagedReference
    private User author;

    public Answer(String text, Question question) {
        this.text = text;
        this.question = question;
    }

    public Answer(String text) {
        this.text = text;
    }

    public Answer(String text, User author) {
        this.text = text;
        this.author = author;
    }
}
