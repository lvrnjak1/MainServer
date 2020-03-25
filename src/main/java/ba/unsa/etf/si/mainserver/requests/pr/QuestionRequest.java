package ba.unsa.etf.si.mainserver.requests.pr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QuestionRequest {
    private String text;
    private String nameSurname;
    private String email;
    private String date;
    private String time;

    public QuestionRequest(String text, String nameSurname, String email, String date, String time) {
        this.text = text;
        this.nameSurname = nameSurname;
        this.email = email;
        this.date = date;
        this.time = time;
    }

    public QuestionRequest(){}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Date getDateFromString() throws ParseException {
        return new SimpleDateFormat("dd.MM.yyyy").parse(date);
    }

    public Date getTimeFromString() throws ParseException {
        return new SimpleDateFormat("HH:mm").parse(time);
    }
}
