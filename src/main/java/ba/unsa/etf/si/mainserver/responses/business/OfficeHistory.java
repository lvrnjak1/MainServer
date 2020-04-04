package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeHistory {
    OfficeResponse officeResponse;
    String startDate;
    String endDate;
    String role;

    public OfficeHistory(EmploymentHistory employmentHistory, OfficeResponse officeResponse){
        this.officeResponse = officeResponse;
        this.role = employmentHistory.getRole();
        this.startDate = employmentHistory.getStringStartDate();
        this.endDate = employmentHistory.getStringEndTime();
    }
}
