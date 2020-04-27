package ba.unsa.etf.si.mainserver.responses.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
    private Long id;
    private int tableNumber;
}

