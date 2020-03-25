package ba.unsa.etf.si.mainserver.models.pr;

import ba.unsa.etf.si.mainserver.models.AuditModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "question_authors")
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

    public QuestionAuthor(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameAndSurname() {
        return nameAndSurname;
    }

    public void setNameAndSurname(String nameAndSurname) {
        this.nameAndSurname = nameAndSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
