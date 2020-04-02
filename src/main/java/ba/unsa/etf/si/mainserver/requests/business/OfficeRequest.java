package ba.unsa.etf.si.mainserver.requests.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeRequest {
    private String address;
    private String city;
    private String country;
    private String email;
    private String phoneNumber;
    private String workDayStart;
    private String workDayEnd;



    public Date getWorkDayStartDateFromString() throws ParseException {
        return workDayStart == null ? null : new SimpleDateFormat("HH:mm").parse(workDayStart);
    }

    public Date getWorkDayEndDateFromString() throws ParseException {
        return workDayEnd == null ? null : new SimpleDateFormat("HH:mm").parse(workDayEnd);
    }
}
