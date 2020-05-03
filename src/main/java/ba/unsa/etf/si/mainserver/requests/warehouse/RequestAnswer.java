package ba.unsa.etf.si.mainserver.requests.warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestAnswer {
    private Long requestId;
    private String message;
}
