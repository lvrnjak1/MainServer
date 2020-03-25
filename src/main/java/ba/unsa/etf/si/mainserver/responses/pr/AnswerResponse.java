package ba.unsa.etf.si.mainserver.responses.pr;

import ba.unsa.etf.si.mainserver.models.pr.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponse {
    private String text;
    private String username;

    public AnswerResponse(Answer answer) {
        this.text = answer.getText();
        this.username = answer.getAuthor().getUsername();
    }
}
