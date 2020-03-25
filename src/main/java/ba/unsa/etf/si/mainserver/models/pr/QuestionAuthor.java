package ba.unsa.etf.si.mainserver.models.pr;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "question_authors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAuthor extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotBlank
    private String nameAndSurname;
    @NotBlank
    private String email;

    @OneToOne(mappedBy = "questionAuthor")
    private Question question;

    public QuestionAuthor(String nameSurname, String email, Question question) {
        this.nameAndSurname = nameSurname;
        this.email = email;
        this.question = question;
    }
}
