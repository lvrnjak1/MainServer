package ba.unsa.etf.si.mainserver.responses.pr;

import ba.unsa.etf.si.mainserver.models.pr.Answer;

public class AnswerResponse {
    private String text;

    public AnswerResponse(Answer answer) {
        this.text = answer.getText();
    }

    public AnswerResponse() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
