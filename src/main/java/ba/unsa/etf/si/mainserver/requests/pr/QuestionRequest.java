package ba.unsa.etf.si.mainserver.requests.pr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequest {
    @NotBlank
    private String text;
    @NotBlank
    private String nameSurname;
    @NotBlank
    private String email;
    @NotBlank
    private String date;
    @NotBlank
    private String time;


    public Date getDateFromString() throws ParseException {
        return date == null ? null : new SimpleDateFormat("dd.MM.yyyy").parse(date);
    }

    public Date getTimeFromString() throws ParseException {
        return time == null ? null : new SimpleDateFormat("HH:mm").parse(time);
    }
}
