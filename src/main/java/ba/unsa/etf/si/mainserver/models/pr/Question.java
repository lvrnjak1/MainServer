package ba.unsa.etf.si.mainserver.models.pr;

import ba.unsa.etf.si.mainserver.models.AuditModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "questions")
public class Question extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String text;

    @Basic
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date date;

    @Basic
    @Temporal(TemporalType.TIME)
    @NotNull
    private Date time;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_author_id", referencedColumnName = "id")
    @NotNull
    private QuestionAuthor questionAuthor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "answer_id", referencedColumnName = "id")
    private Answer answer = null;


    public Question(String text, QuestionAuthor questionAuthor, Date date, Date time) {
        this.text = text;
        this.questionAuthor = questionAuthor;
        this.date = date;
        this.time = time;
    }

    public Question(){}

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

    public QuestionAuthor getQuestionAuthor() {
        return questionAuthor;
    }

    public void setQuestionAuthor(QuestionAuthor questionAuthor) {
        this.questionAuthor = questionAuthor;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getStringDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(getDate());
    }

    public String getStringTime(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(getTime());
    }
}
