package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.ContactInformation;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmploymentHistoryRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.auth.RegistrationRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeProfileService {
    private final EmployeeProfileRepository employeeProfileRepository;
    private final BusinessService businessService;
    private final OfficeProfileRepository officeProfileRepository;
    private final EmploymentHistoryRepository employmentHistoryRepository;
    private final EmployeeActivityRepository employeeActivityRepository;
    private final OfficeService officeService;

    public EmployeeProfileService(EmployeeProfileRepository employeeProfileRepository, BusinessService businessService, OfficeProfileRepository officeProfileRepository, EmploymentHistoryRepository employmentHistoryRepository, EmployeeActivityRepository employeeActivityRepository, OfficeService officeService) {
        this.employeeProfileRepository = employeeProfileRepository;
        this.businessService = businessService;
        this.officeProfileRepository = officeProfileRepository;
        this.employmentHistoryRepository = employmentHistoryRepository;
        this.employeeActivityRepository = employeeActivityRepository;
        this.officeService = officeService;
    }

    public List<EmployeeProfile> findAllByOptionalBusinessId(Long businessId) {
        if (businessId != null) {
            return employeeProfileRepository.findAllByBusinessId(businessId);
        }
        return employeeProfileRepository.findAll();
    }

    public EmployeeProfile findEmployeeById(Long employeeId) {
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileRepository.findById(employeeId);
        if (!optionalEmployeeProfile.isPresent()) {
            throw new ResourceNotFoundException("Employee doesn't exist");
        }
        checkEmployeeActivity(optionalEmployeeProfile.get());
        return optionalEmployeeProfile.get();
    }

    public void checkEmployeeActivity(EmployeeProfile employeeProfile){
        Optional<EmployeeActivity> employeeActivity = employeeActivityRepository.findByEmployeeProfile(employeeProfile);
        if(employeeActivity.isPresent()){
            //ova osoba je inactive employee
            throw new ResourceNotFoundException("This employee doesn't exist");
        }
    }

    public EmployeeProfile save(EmployeeProfile employeeProfile) {
        return employeeProfileRepository.save(employeeProfile);
    }

    public EmployeeProfile findEmployeeByAccount(User account) {
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileRepository.findByAccountId(account.getId());
        if (!optionalEmployeeProfile.isPresent()) {
            throw new BadParameterValueException("User is not an employee");
        }
        checkEmployeeActivity(optionalEmployeeProfile.get());
        return optionalEmployeeProfile.get();
    }

    public EmployeeProfile createEmployeeProfile(RegistrationRequest registrationRequest, User user) throws ParseException {
        Business employeeBusiness = null;
        try {
            employeeBusiness = businessService.findBusinessById(registrationRequest.getBusinessId() != null ? registrationRequest.getBusinessId() : 0);
        }
        catch (ResourceNotFoundException e){
        }
        EmployeeProfile employeeProfile = new EmployeeProfile(
                registrationRequest.getName(),
                registrationRequest.getSurname(),
                registrationRequest.getDateFromString(),
                registrationRequest.getJmbg(),
                new ContactInformation(
                        registrationRequest.getAddress(),
                        registrationRequest.getCity(),
                        registrationRequest.getCountry(),
                        registrationRequest.getEmail(),
                        registrationRequest.getPhoneNumber()
                ),
                user,
                employeeBusiness
                );
        return employeeProfileRepository.save(employeeProfile);
    }

    public void assignEmployeeToOffice(EmployeeProfile employeeProfile, Office office, String role){
        Optional<OfficeProfile> optionalOfficeProfile = officeProfileRepository
                .findByEmployeeIdAndOfficeId(employeeProfile.getId(), office.getId());
        if(!optionalOfficeProfile.isPresent()) { //nije vec zaposlen u ovom officeu
            officeProfileRepository.save(new OfficeProfile(office, employeeProfile));
        }
        else { //vec zaposlen u ovom ofisu
            unassignEmployeeFromOffice(employeeProfile, office);
            officeProfileRepository.save(new OfficeProfile(office, employeeProfile));
        }
        createNewEmployment(employeeProfile, office, role);
    }

    public void createNewEmployment(EmployeeProfile employeeProfile, Office office, String role){
        EmploymentHistory employmentHistory = null;
        if(office != null) {
                    employmentHistory = new EmploymentHistory(employeeProfile.getId(), office.getId(), new Date(), null, role);
        }
        else{
            employmentHistory = new EmploymentHistory(employeeProfile.getId(), null , new Date(), null, role);
        }
        employmentHistoryRepository.save(employmentHistory);
    }

    public void endEmployment(EmployeeProfile employeeProfile, Office office, String role){
        List<EmploymentHistory> employmentHistoryList = null;
        if (office != null) {
            employmentHistoryList = employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole
                    (employeeProfile.getId(), office.getId(), role);
        }
        else{
            employmentHistoryList = employmentHistoryRepository.findAllByEmployeeProfileIdAndRole
                    (employeeProfile.getId(), role);
        }
        employmentHistoryList.stream()
                .filter(employmentHistory -> employmentHistory.getEndDate() == null)
                .forEach(employmentHistory -> {
                    employmentHistory.setEndDate(new Date());
                    employmentHistoryRepository.save(employmentHistory);
                });
    }

    public void unassignEmployee(EmployeeProfile employeeProfile){
        officeService.findAllByManager(employeeProfile).forEach(office -> {
            office.setManager(null);
            officeService.save(office);
        });
        officeProfileRepository.findAllByEmployeeId(employeeProfile.getId())
                .forEach(officeProfile -> unassignEmployeeFromOffice(employeeProfile,officeProfile.getOffice()));
    }

    public void unassignEmployeeFromOffice(EmployeeProfile employeeProfile, Office office){
        Optional<OfficeProfile> officeProfile = officeProfileRepository.findByEmployeeIdAndOfficeId(employeeProfile.getId(), office.getId());
        if(!officeProfile.isPresent()){
            throw new AppException("This office doesn't hire this employee");
        }
        endEmployment(employeeProfile, office, "ROLE_CASHIER");
        endEmployment(employeeProfile, office,"ROLE_BARTENDER");
        endEmployment(employeeProfile, office, "ROLE_OFFICEMAN");
        officeProfileRepository.delete(officeProfile.get());
    }

    public void deleteAllEmployeesFromOffice(Office office){
        officeProfileRepository.findAllByOfficeIdAndOffice_BusinessId(office.getId(),office.getBusiness().getId())
                .forEach(officeProfileRepository::delete);
        employmentHistoryRepository.findAllByOfficeId(office.getId())
                .forEach(employmentHistoryRepository::delete);

    }

    public void fireEmployee(EmployeeProfile employeeProfile){
        officeProfileRepository.findAllByEmployeeId(employeeProfile.getId())
                .forEach(officeProfileRepository::delete);

        employmentHistoryRepository.findAllByEmployeeProfileId(employeeProfile.getId())
                .forEach(employmentHistoryRepository::delete);

        EmployeeActivity employeeActivity = new EmployeeActivity();
        employeeActivity.setAccount(employeeProfile.getAccount());
        employeeActivity.setEmployeeProfile(employeeProfile);
        employeeActivityRepository.save(employeeActivity);
    }

}
