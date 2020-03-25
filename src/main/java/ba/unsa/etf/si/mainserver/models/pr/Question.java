package ba.unsa.etf.si.mainserver.models.pr;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "questions")
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public String getStringDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(getDate());
    }

    public String getStringTime(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(getTime());
    }
}
