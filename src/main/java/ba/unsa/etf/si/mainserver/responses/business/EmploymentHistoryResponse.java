package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.Notification;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentHistoryResponse {
    EmployeeProfileResponse employeeProfileResponse;
    List<OfficeResponse> officeResponse;
    String startDate;
    String endDate;

    public EmploymentHistoryResponse(EmploymentHistory employmentHistory, EmployeeProfile employeeProfile,
                                     List<OfficeResponse> officeResponse){
        this.employeeProfileResponse = new EmployeeProfileResponse(employeeProfile);
        this.officeResponse = officeResponse;
        this.startDate = employmentHistory.getStringStartDate();
        this.endDate = employmentHistory.getStringEndTime();
    }
}
