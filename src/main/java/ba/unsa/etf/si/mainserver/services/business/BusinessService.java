package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.*;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.*;
import ba.unsa.etf.si.mainserver.requests.auth.RegistrationRequest;
import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import ba.unsa.etf.si.mainserver.responses.business.BusinessResponse;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final OfficeRepository officeRepository;
    private final OfficeService officeService;
    private final UserService userService;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeActivityRepository employeeActivityRepository;
    private final ServerOfficeRepository serverOfficeRepository;
    private final OfficeProfileRepository officeProfileRepository;

    public BusinessService(BusinessRepository businessRepository,
                           OfficeRepository officeRepository,
                           CashRegisterRepository cashRegisterRepository,
                           OfficeService officeService, UserService userService,
                           EmployeeProfileRepository employeeProfileRepository,
                           EmployeeActivityRepository employeeActivityRepository,
                           ServerOfficeRepository serverOfficeRepository, OfficeProfileRepository officeProfileRepository) {
        this.businessRepository = businessRepository;
        this.officeRepository = officeRepository;
        this.officeService = officeService;
        this.userService = userService;
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeActivityRepository = employeeActivityRepository;
        this.serverOfficeRepository = serverOfficeRepository;
        this.officeProfileRepository = officeProfileRepository;
    }

    public Business save(Business business) {
        return businessRepository.save(business);
    }

    public List<Business> findAll() {
        return businessRepository.findAll();
    }

    public Business findBusinessById(Long businessId){
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business with id " + businessId + " doesn't exist"));
    }

    public Business findByName(String businessName) {
        Optional<Business> optionalBusiness = businessRepository.findByName(businessName);
        if (!optionalBusiness.isPresent()) {
            throw new ResourceNotFoundException("No such business with name " + businessName);
        }
        return optionalBusiness.get();
    }

    public Business findBusinessOfCurrentUser(UserPrincipal userPrincipal) {
        User user = userService.findUserByUsername(userPrincipal.getUsername());
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileRepository.findByAccountId(user.getId());
        if (!optionalEmployeeProfile.isPresent()) {
            throw new BadParameterValueException("User is not an employee");
        }
        Optional<EmployeeActivity> employeeActivity = employeeActivityRepository.findByEmployeeProfile(optionalEmployeeProfile.get());
        if(employeeActivity.isPresent()){
            //ova osoba je inactive employee
            throw new ResourceNotFoundException("This employee doesn't exist");
        }
        return findBusinessById(optionalEmployeeProfile.get().getBusiness().getId());
    }

    public List<BusinessResponse> getAllBusinessResponses() {
        List<Business> businesses = businessRepository.findAll();
        return businesses
                .stream()
                .map(
                        business -> new BusinessResponse(
                                business,
                                officeService
                                        .getAllOfficeResponsesByBusinessId(
                                                business.getId()
                                        )
                        )
                ).collect(Collectors.toList());
    }

    public void checkIfTablesAvailable(Business business) {
        if(!business.isRestaurantFeature()){
            throw new AppException("This business is not a restaurant!");
        }
    }

    public int countOfficesInBusiness(Long businessId){
        return officeRepository.findAllByBusinessId(businessId).size();
    }

    public void createServer(Business business, Office office,
                                                  @NotBlank String username,
                                                  @NotBlank String password) throws ParseException {
        RoleResponse roleResponse = new RoleResponse(null, "ROLE_SERVER");
        RegistrationRequest registration = new RegistrationRequest(
                username, password, username+"@email.com", Collections.singletonList(roleResponse),
                "cash_server_"+username, "cash_server_"+username,
                "01.01.2020", "123456789", "address", "city",
                "country", "123", business.getId()
        );
        userService.checkAvailability(registration);
        User user = userService.createUserAccount(registration); //and saves it
        EmployeeProfile employeeProfile = createEmployeeProfile(registration, user); //and saves it

        OfficeProfile officeProfile = new OfficeProfile(office, employeeProfile); //employ this employee in office
        officeProfileRepository.save(officeProfile);

        ServerOffice serverOffice = new ServerOffice(office, user); //mark this employee as server
        serverOfficeRepository.save(serverOffice);
    }

    public EmployeeProfile createEmployeeProfile(RegistrationRequest registrationRequest, User user) throws ParseException {
        Business employeeBusiness = null;
        try {
            employeeBusiness = findBusinessById(registrationRequest.getBusinessId() != null ? registrationRequest.getBusinessId() : 0);
        }
        catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("UPS kontaktirajte Lamiju");
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

}
