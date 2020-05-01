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
@AllArgsConstructor
@NoArgsConstructor
public class SyncTimeRequest {
    @NotBlank
    private String syncTime;

    @JsonIgnore
    public Date getSyncTimeFromString() throws ParseException {
        return syncTime == null ? null : new SimpleDateFormat("HH:mm").parse(syncTime);
    }
}
