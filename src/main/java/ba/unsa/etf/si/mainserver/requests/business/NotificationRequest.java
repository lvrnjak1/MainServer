package ba.unsa.etf.si.mainserver.requests.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private Long employeeId;
    private boolean hired;
    private String date;
    private String time;

    public Date getDateFromString() throws ParseException {
        return date == null ? null : new SimpleDateFormat("dd.MM.yyyy").parse(date);
    }

    public Date getTimeFromString() throws ParseException {
        return time == null ? null : new SimpleDateFormat("HH:mm").parse(time);
    }
}