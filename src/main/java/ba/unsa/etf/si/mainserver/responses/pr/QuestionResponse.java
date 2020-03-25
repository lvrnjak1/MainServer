package ba.unsa.etf.si.mainserver.responses.pr;

import ba.unsa.etf.si.mainserver.models.pr.Question;

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

    public QuestionResponse() {
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

    public String getAuthorNameAndSurname() {
        return authorNameAndSurname;
    }

    public void setAuthorNameAndSurname(String authorNameAndSurname) {
        this.authorNameAndSurname = authorNameAndSurname;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public AnswerResponse getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerResponse answer) {
        this.answer = answer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
