package ba.unsa.etf.si.mainserver.services.business;

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

    public EmployeeProfileService(EmployeeProfileRepository employeeProfileRepository, BusinessService businessService, OfficeProfileRepository officeProfileRepository, EmploymentHistoryRepository employmentHistoryRepository, EmployeeActivityRepository employeeActivityRepository) {
        this.employeeProfileRepository = employeeProfileRepository;
        this.businessService = businessService;
        this.officeProfileRepository = officeProfileRepository;
        this.employmentHistoryRepository = employmentHistoryRepository;
        this.employeeActivityRepository = employeeActivityRepository;
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
            List<EmploymentHistory> employmentHistoryList =
                    employmentHistoryRepository.findAllByEmployeeProfileId(employeeProfile.getId());
            //mora biti samo jedan
            for(EmploymentHistory employmentHistory: employmentHistoryList){
                if(employmentHistory.getEndDate() == null){
                    employmentHistory.setEndDate(new Date());
                    employmentHistoryRepository.save(employmentHistory);
                    break;
                }
            }
        }
        EmploymentHistory employmentHistory =
                new EmploymentHistory(employeeProfile.getId(),office.getId(), new Date(), null, role);
        employmentHistoryRepository.save(employmentHistory);
    }


    public void unassignEmployeeFromPosition(EmployeeProfile employeeProfile, Office office, String role) {
        Optional<OfficeProfile> officeProfileOptional = officeProfileRepository
                .findByEmployeeIdAndOfficeId(employeeProfile.getId(), office.getId());
        if(!officeProfileOptional.isPresent()){
            throw new BadParameterValueException("This employee is not asssigned to this office");
        }
        OfficeProfile officeProfile = officeProfileOptional.get();
        officeProfileRepository.delete(officeProfile); //ne radi vise
        List<EmploymentHistory> employmentHistoryList =
                employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole(employeeProfile.getId(), office.getId(), role);
        //mora biti samo jedan
        for(EmploymentHistory employmentHistory: employmentHistoryList){
            if(employmentHistory.getEndDate() == null){
                employmentHistory.setEndDate(new Date());
                employmentHistoryRepository.save(employmentHistory);
                break;
            }
        }
    }

    public void unassignEmployeeFromOffice(OfficeProfile officeProfile){
        List<EmploymentHistory> employmentHistoryList =
                employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole
                        (officeProfile.getEmployee().getId(), officeProfile.getOffice().getId(),"ROLE_CASHIER");
        List<EmploymentHistory> employmentHistoryList2 =
                employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole
                        (officeProfile.getEmployee().getId(), officeProfile.getOffice().getId(),"ROLE_BARTENDER");
        List<EmploymentHistory> employmentHistoryList3 =
                employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole
                        (officeProfile.getEmployee().getId(), officeProfile.getOffice().getId(),"ROLE_OFFICEMAN");
        employmentHistoryList.addAll(employmentHistoryList2);
        employmentHistoryList.addAll(employmentHistoryList3);

        for(EmploymentHistory employmentHistory: employmentHistoryList){
            if(employmentHistory.getEndDate() == null){
                employmentHistory.setEndDate(new Date());
                employmentHistoryRepository.save(employmentHistory);
                break;
            }
        }
        officeProfileRepository.delete(officeProfile);
    }

    public void unassignAllEmployeesFromOffice(Office office){
        List<OfficeProfile> officeProfiles =
                officeProfileRepository.findAllByOfficeIdAndOffice_BusinessId(office.getId(),office.getBusiness().getId());
        if(!officeProfiles.isEmpty()){
            officeProfiles.forEach(officeProfile -> unassignEmployeeFromOffice(officeProfile));
        }
    }

    public void fireEmployee(EmployeeProfile employeeProfile){
        List<OfficeProfile> officeProfiles = officeProfileRepository.findAllByEmployeeId(employeeProfile.getId());
        for(OfficeProfile officeProfile : officeProfiles){
            officeProfileRepository.delete(officeProfile);
        }

        List<EmploymentHistory> employmentHistoryList = employmentHistoryRepository.findAllByEmployeeProfileId(employeeProfile.getId());
        for(EmploymentHistory employmentHistory : employmentHistoryList){
            employmentHistoryRepository.delete(employmentHistory);
        }

        EmployeeActivity employeeActivity = new EmployeeActivity();
        employeeActivity.setAccount(employeeProfile.getAccount());
        employeeActivity.setEmployeeProfile(employeeProfile);
        employeeActivityRepository.save(employeeActivity);
    }

}
