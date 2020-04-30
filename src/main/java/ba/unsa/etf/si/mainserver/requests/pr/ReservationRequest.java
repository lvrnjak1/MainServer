package ba.unsa.etf.si.mainserver.requests.pr;

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
public class ReservationRequest {
    private Long tableId;
    private String name;
    private String surname;
    private String email;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime reservationDateTime;
}
