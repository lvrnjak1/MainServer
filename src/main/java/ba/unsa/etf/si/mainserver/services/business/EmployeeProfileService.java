package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.models.business.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeProfileService {
    private final EmployeeProfileRepository employeeProfileRepository;

    public EmployeeProfileService(EmployeeProfileRepository employeeProfileRepository) {
        this.employeeProfileRepository = employeeProfileRepository;
    }

    public Optional<EmployeeProfile> findById(Long id) {
        return employeeProfileRepository.findById(id);
    }
}
