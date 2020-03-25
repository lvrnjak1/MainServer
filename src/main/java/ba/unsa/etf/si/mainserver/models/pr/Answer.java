package ba.unsa.etf.si.mainserver.models.pr;

import ba.unsa.etf.si.mainserver.models.AuditModel;

import javax.persistence.*;

@Entity
@Table(name = "answers")
public class Answer extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    private String text;

    @OneToOne(mappedBy = "answer")
    private Question question;

    //TODO
    //Add answerAuthor that has to be PR worker

    public Answer() {
    }

    public Answer(String text, Question question) {
        this.text = text;
        this.question = question;
    }

    public Answer(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
