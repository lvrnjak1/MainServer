package ba.unsa.etf.si.mainserver.requests;

public class AnswerRequest {
    private String text;

    public AnswerRequest(String text) {
        this.text = text;
    }

    public AnswerRequest() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
