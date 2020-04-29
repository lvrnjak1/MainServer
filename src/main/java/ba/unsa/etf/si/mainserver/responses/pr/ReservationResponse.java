package ba.unsa.etf.si.mainserver.responses.pr;

import ba.unsa.etf.si.mainserver.responses.business.TableResponse;
import ba.unsa.etf.si.mainserver.serializers.CustomLocalDateTimeDeserializer;
import ba.unsa.etf.si.mainserver.serializers.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private String status;
    private TableResponse table;
    private String name;
    private String surname;
    private String email;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime reservationDateTime;

    private Long verificationCode;
}
