package ba.unsa.etf.si.mainserver.responses.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashRegisterProfitResponse {
    private Long id;
    private String name;
    private BigDecimal dailyProfit;
    private BigDecimal totalProfit;
}
