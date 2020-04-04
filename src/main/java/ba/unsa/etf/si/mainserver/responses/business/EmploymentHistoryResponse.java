package ba.unsa.etf.si.mainserver.responses.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentHistoryResponse {
    EmployeeProfileResponse employeeProfileResponse;
    List<OfficeHistory> officeHistories;



}
