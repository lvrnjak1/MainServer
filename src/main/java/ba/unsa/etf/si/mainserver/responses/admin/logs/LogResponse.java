package ba.unsa.etf.si.mainserver.responses.admin.logs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogResponse {
    private String username;

    private long timestamp;

    private SimpleActionResponse action;
}
