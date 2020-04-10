package ba.unsa.etf.si.mainserver.responses.admin.logs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogCollectionResponse {
    private List<LogResponse> logs;
}
