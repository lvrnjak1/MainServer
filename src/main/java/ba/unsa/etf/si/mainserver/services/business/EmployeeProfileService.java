package ba.unsa.etf.si.mainserver.services.business;

import org.springframework.stereotype.Service;

@Service
public class EmployeeProfileService {
    private final EmployeeProfileService employeeProfileService;

    public EmployeeProfileService(EmployeeProfileService employeeProfileService) {
        this.employeeProfileService = employeeProfileService;
    }
}
