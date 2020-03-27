package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.ContactInformation;
import ba.unsa.etf.si.mainserver.models.business.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.auth.RegistrationRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeProfileService {
    private final EmployeeProfileRepository employeeProfileRepository;
    private final BusinessService businessService;

    public EmployeeProfileService(EmployeeProfileRepository employeeProfileRepository, BusinessService businessService) {
        this.employeeProfileRepository = employeeProfileRepository;
        this.businessService = businessService;
    }

    public List<EmployeeProfile> findAllByOptionalBusinessId(Long ...businessId) {
        if (businessId != null) {
            return employeeProfileRepository.findAllByBusinessId(businessId[0]);
        }
        return employeeProfileRepository.findAll();
    }

    public Optional<EmployeeProfile> findById(Long id) {
        return employeeProfileRepository.findById(id);
    }

    public EmployeeProfile save(EmployeeProfile employeeProfile) {
        return employeeProfileRepository.save(employeeProfile);
    }

    public Optional<EmployeeProfile> findByAccount(User account) {
        return employeeProfileRepository.findByAccountId(account.getId());
    }

    public EmployeeProfile createEmployeeProfile(RegistrationRequest registrationRequest, User user) {
        Optional<Business> optionalBusiness = businessService.findById(registrationRequest.getBusinessId());
        EmployeeProfile employeeProfile = new EmployeeProfile(
                registrationRequest.getName(),
                registrationRequest.getSurname(),
                new ContactInformation(
                        registrationRequest.getAddress(),
                        registrationRequest.getCity(),
                        registrationRequest.getCountry(),
                        registrationRequest.getEmail(),
                        registrationRequest.getPhoneNumber()
                ),
                user,
                optionalBusiness.orElse(null)
                );
        return employeeProfileRepository.save(employeeProfile);
    }

}
