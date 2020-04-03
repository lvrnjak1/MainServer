package ba.unsa.etf.si.mainserver.requests.business;

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
public class OfficeRequest {
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String workDayStart;
    @NotBlank
    private String workDayEnd;



    public Date getWorkDayStartDateFromString() throws ParseException {
        return workDayStart == null ? null : new SimpleDateFormat("HH:mm").parse(workDayStart);
    }

    public Date getWorkDayEndDateFromString() throws ParseException {
        return workDayEnd == null ? null : new SimpleDateFormat("HH:mm").parse(workDayEnd);
    }
}
