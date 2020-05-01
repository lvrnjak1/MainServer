package ba.unsa.etf.si.mainserver.requests.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkHoursRequest {
    @NotBlank
    private String start;
    @NotBlank
    private String end;

    @JsonIgnore
    public Date getStartTimeFromString() throws ParseException {
        return start == null ? null : new SimpleDateFormat("HH:mm").parse(start);
    }
    @JsonIgnore
    public Date getEndTimeFromString() throws ParseException {
        return end == null ? null : new SimpleDateFormat("HH:mm").parse(end);
    }
}
