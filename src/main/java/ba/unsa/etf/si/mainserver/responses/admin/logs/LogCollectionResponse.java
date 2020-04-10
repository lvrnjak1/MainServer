package ba.unsa.etf.si.mainserver.responses.admin.logs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LogCollectionResponse {
    private List<LogResponse> logs;
}
