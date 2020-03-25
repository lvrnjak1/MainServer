package ba.unsa.etf.si.mainserver.responses.pr;

import ba.unsa.etf.si.mainserver.models.pr.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    private Long id;
    private String text;
    private String authorNameAndSurname;
    private String authorEmail;
    private AnswerResponse answer;
    private String date;
    private String time;

    public QuestionResponse(Question question) {
        this.id = question.getId();
        this.text = question.getText();
        this.authorNameAndSurname = question.getQuestionAuthor().getNameAndSurname();
        this.authorEmail = question.getQuestionAuthor().getEmail();
        this.answer = new AnswerResponse();
        if(question.getAnswer() != null){
            this.answer = new AnswerResponse(question.getAnswer());
        }
        this.date = question.getStringDate();
        this.time = question.getStringTime();
    }
}
