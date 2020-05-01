package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.configurations.Actions;
import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmploymentHistoryRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationPayload;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.UserResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RegistrationResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import ba.unsa.etf.si.mainserver.responses.business.EmployeeProfileResponse;
import ba.unsa.etf.si.mainserver.responses.business.EmploymentHistoryResponse;
import ba.unsa.etf.si.mainserver.responses.business.OfficeHistory;
import ba.unsa.etf.si.mainserver.responses.business.OfficeResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.CashRegisterService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final UserService userService;
    private final EmployeeProfileService employeeProfileService;
    private final OfficeProfileRepository officeProfileRepository;
    private final BusinessService businessService;
    private final EmploymentHistoryRepository employmentHistoryRepository;
    private final EmployeeActivityRepository employeeActivityRepository;
    private final OfficeService officeService;
    private final CashRegisterService cashRegisterService;
    private final LogServerService logServerService;

    public EmployeeController(UserService userService, EmployeeProfileService employeeProfileService,
                              OfficeProfileRepository officeProfileRepository,
                              BusinessService businessService,
                              EmploymentHistoryRepository employmentHistoryRepository, EmployeeActivityRepository employeeActivityRepository, OfficeService officeService, CashRegisterService cashRegisterService, LogServerService logServerService) {
        this.userService = userService;
        this.employeeProfileService = employeeProfileService;
        this.officeProfileRepository = officeProfileRepository;
        this.businessService = businessService;
        this.employmentHistoryRepository = employmentHistoryRepository;
        this.employeeActivityRepository = employeeActivityRepository;
        this.officeService = officeService;
        this.cashRegisterService = cashRegisterService;
        this.logServerService = logServerService;
    }

    @GetMapping("/offices/{officeId}/employees")
    @Secured({"ROLE_MERCHANT", "ROLE_MANAGER"})
    public List<UserResponse> getEmployees(@CurrentUser UserPrincipal userPrincipal,
                                           @PathVariable Long officeId) {
        User user = userService.findUserByUsername(userPrincipal.getUsername());
        EmployeeProfile employeeProfile2 = employeeProfileService.findEmployeeByAccount(user);
        if (employeeProfile2.getBusiness() == null) {
            throw new AppException("Not an employee");
        }
        List<OfficeProfile> officeProfiles = officeProfileRepository.findAllByOfficeIdAndOffice_BusinessId(
                officeId,
                employeeProfile2.getBusiness().getId()
        );
        List<EmployeeActivity> employeeActivities = employeeActivityRepository.findAll();
        return employeeProfileService
                .findAllByOptionalBusinessId(employeeProfile2.getBusiness().getId())
                .stream().filter(employeeProfile -> officeProfiles.stream().anyMatch(officeProfile -> officeProfile.getEmployee().getId().equals(employeeProfile.getId())))
                .map(
                        employeeProfile -> new UserResponse(
                                employeeProfile.getAccount().getId(),
                                employeeProfile.getAccount().getUsername(),
                                employeeProfile.getAccount().getEmail(),
                                employeeProfile.getName(),
                                employeeProfile.getSurname(),
                                employeeProfile.getStringDate(),
                                employeeProfile.getJmbg(),
                                employeeProfile.getContactInformation().getAddress(),
                                employeeProfile.getContactInformation().getPhoneNumber(),
                                employeeProfile.getContactInformation().getCountry(),
                                employeeProfile.getContactInformation().getCity(),
                                employeeProfile.getAccount())
                )
                .filter(
                        userResponse ->
                                employeeActivities
                                        .stream()
                                        .noneMatch(
                                                employeeActivity ->
                                                        employeeActivity
                                                                .getAccount()
                                                                .getId()
                                                                .equals(userResponse.getUserId())
                                        )
                )
                .collect(Collectors.toList());
    }

    //MORA BITI EMPLOYEE SAMO U JEDNOM OFISU ZA OVU RUTU, INACE PADA
    @GetMapping("/office-employees")
    @Secured("ROLE_OFFICEMAN")
    public List<RegistrationResponse> getOfficeEmployees(@CurrentUser UserPrincipal userPrincipal) {
        User user2 = userService.findUserByUsername(userPrincipal.getUsername());
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user2);
        Optional<OfficeProfile> optionalOfficeProfile = officeProfileRepository.findByEmployee_Id(employeeProfile.getId());
        if (!optionalOfficeProfile.isPresent()) {
            throw new ResourceNotFoundException("No Office");
        }
        Office office = optionalOfficeProfile.get().getOffice();
        List<OfficeProfile> officeProfiles = officeProfileRepository.findAllByOfficeIdAndOffice_BusinessId(office.getId(), office.getBusiness().getId());
        List<EmployeeActivity> employeeActivities = employeeActivityRepository.findAll();
        return officeProfiles
                .stream()
                .map(
                        officeProfile -> {
                            EmployeeProfile employee = officeProfile.getEmployee();
                            User user = employee.getAccount();
                            return new RegistrationResponse(
                                    user.getId(),
                                    user.getUsername(),
                                    user.getPassword(),
                                    user.getEmail(),
                                    user.isOtp(),
                                    user.getRoles()
                                            .stream()
                                            .map(role -> new RoleResponse(role.getId(), role.getName().name()))
                                            .collect(Collectors.toList()),
                                    new EmployeeProfileResponse(employee));
                        }
                )
                .filter(
                        userResponse ->
                                employeeActivities
                                        .stream()
                                        .noneMatch(
                                                employeeActivity ->
                                                        employeeActivity
                                                                .getAccount()
                                                                .getId()
                                                                .equals(userResponse.getId())
                                        )
                )
                .collect(Collectors.toList());
    }

    @GetMapping("/employees")
    @Secured({"ROLE_MERCHANT", "ROLE_MANAGER"})
    public List<UserResponse> getEmployees(@CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findUserByUsername(userPrincipal.getUsername());
        EmployeeProfile employeeProfile2 = employeeProfileService.findEmployeeByAccount(user);
        List<EmployeeActivity> employeeActivities = employeeActivityRepository.findAll();
        return employeeProfileService
                .findAllByOptionalBusinessId(employeeProfile2.getBusiness().getId())
                .stream()
                .map(
                        employeeProfile -> new UserResponse(
                                employeeProfile.getAccount().getId(),
                                employeeProfile.getAccount().getUsername(),
                                employeeProfile.getAccount().getEmail(),
                                employeeProfile.getName(),
                                employeeProfile.getSurname(),
                                employeeProfile.getStringDate(),
                                employeeProfile.getJmbg(),
                                employeeProfile.getContactInformation().getAddress(),
                                employeeProfile.getContactInformation().getPhoneNumber(),
                                employeeProfile.getContactInformation().getCountry(),
                                employeeProfile.getContactInformation().getCity(),
                                employeeProfile.getAccount())
                )
                .filter(
                        userResponse ->
                                employeeActivities
                                        .stream()
                                        .noneMatch(
                                                employeeActivity ->
                                                        employeeActivity
                                                                .getAccount()
                                                                .getId()
                                                                .equals(userResponse.getUserId())
                                        )
                )
                .collect(Collectors.toList());
    }

    @DeleteMapping("/employees/{userId}")
    @Secured({"ROLE_MANAGER", "ROLE_MERCHANT"})
    public ResponseEntity<ApiResponse> fireEmployee(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long userId) {
        User user = userService.findUserById(userId);
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);
        if(user.getRoles().stream().anyMatch(role -> role.getName().toString().equals("ROLE_MERCHANT"))){
            throw new UnauthorizedException("Merchant cannot get fired");
        }
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        if (!business.getId().equals(employeeProfile.getBusiness().getId())) {
            throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
        }

        officeService.findAllByManager(employeeProfile).forEach(office -> {
            office.setManager(null);
            officeService.save(office);
        });

        employeeProfileService.fireEmployee(employeeProfile);
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.FIRE_EMPLOYEE_ACTION_NAME,
                "employee",
                "Employee " + userPrincipal.getUsername() + " has fired employee " + employeeProfile.getName()
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "warning",
                        new NotificationPayload(
                                userPrincipal.getUsername(),
                                "fire_employee",
                                employeeProfile.getName() + " " + employeeProfile.getSurname() + " has been fired."
                        )
                ),
                "merchant_dashboard"
        );
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "warning",
                        new NotificationPayload(
                                userPrincipal.getUsername(),
                                "fire_employee",
                                employeeProfile.getName() + " " + employeeProfile.getSurname() + " has been fired."
                        )
                ),
                "admin"
        );
        return ResponseEntity.ok(new ApiResponse("Employee successfully fired", 200));
    }

    @GetMapping("/business/employees/{userId}/history")
    @Secured("ROLE_MANAGER")
    public EmploymentHistoryResponse getEmployeeHistory(@PathVariable Long userId,
                                                        @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        User user = userService.findUserById(userId);
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);

        if(!employeeProfile.getBusiness().getId().equals(business.getId())){
            throw new BadParameterValueException("Employee with this id doesn't exist");
        }

        List<EmploymentHistory> employmentHistoryList = employmentHistoryRepository.findAllByEmployeeProfileId(employeeProfile.getId());
        if(employmentHistoryList.isEmpty()){
            throw new BadParameterValueException("Employee doesn't have employment history!");
        }

        List<OfficeHistory> responseList = new ArrayList<>();
        for(EmploymentHistory employmentHistory : employmentHistoryList){
            OfficeResponse officeResponse = null;
            if(employmentHistory.getOfficeId() != null){
                Office officeOptional = officeService.findOfficeById(employmentHistory.getOfficeId(), business.getId());
                officeResponse = new OfficeResponse(officeOptional,cashRegisterService.getAllCashRegisterResponsesByOfficeId(officeOptional.getId()));
            }
            responseList.add(new OfficeHistory(employmentHistory,officeResponse));
        }

        return new EmploymentHistoryResponse(new EmployeeProfileResponse(employeeProfile), responseList);
    }
}
