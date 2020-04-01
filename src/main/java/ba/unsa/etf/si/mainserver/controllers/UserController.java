package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.business.EmployeeProfileRequest;
import ba.unsa.etf.si.mainserver.requests.business.RoleChangeRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.UserResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RegistrationResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import ba.unsa.etf.si.mainserver.responses.business.EmployeeProfileResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final EmployeeProfileService employeeProfileService;
    private final OfficeProfileRepository officeProfileRepository;
    private final EmployeeActivityRepository employeeActivityRepository;
    private final BusinessService businessService;
    private final OfficeService officeService;

    public UserController(UserService userService, EmployeeProfileService employeeProfileService,
                          OfficeProfileRepository officeProfileRepository,
                          EmployeeActivityRepository employeeActivityRepository,
                          BusinessService businessService, OfficeService officeService) {
        this.userService = userService;
        this.employeeProfileService = employeeProfileService;
        this.officeProfileRepository = officeProfileRepository;
        this.employeeActivityRepository = employeeActivityRepository;
        this.businessService = businessService;
        this.officeService = officeService;
    }

    @GetMapping("/users")
    @Secured("ROLE_ADMIN")
    public List<UserResponse> getUsers(@RequestParam(required = false) Long businessId) {
       return employeeProfileService.findAllByOptionalBusinessId(businessId)
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
                .collect(Collectors.toList());
    }

    @GetMapping("/office-employees")
    @Secured("ROLE_OFFICEMAN")
    public List<RegistrationResponse> getOfficeEmployees(@CurrentUser UserPrincipal userPrincipal) {
        Optional<User> optionalUser = userService.findByUsername(userPrincipal.getUsername());
        if (!optionalUser.isPresent()) {
            throw new AppException("Horror");
        }
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileService.findByAccount(optionalUser.get());
        if (!optionalEmployeeProfile.isPresent() || optionalEmployeeProfile.get().getBusiness() == null) {
            throw new AppException("Not an employee");
        }
        Optional<OfficeProfile> optionalOfficeProfile = officeProfileRepository.findByEmployee_Id(optionalEmployeeProfile.get().getId());
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
        Optional<User> optionalUser = userService.findByUsername(userPrincipal.getUsername());
        if (!optionalUser.isPresent()) {
            throw new AppException("Horror");
        }
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileService.findByAccount(optionalUser.get());
        if (!optionalEmployeeProfile.isPresent() || optionalEmployeeProfile.get().getBusiness() == null) {
            throw new AppException("Not an employee");
        }
        List<EmployeeActivity> employeeActivities = employeeActivityRepository.findAll();
        return employeeProfileService
                .findAllByOptionalBusinessId(optionalEmployeeProfile.get().getBusiness().getId())
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
        Optional<User> optionalUser = userService.findUserById(userId);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User does not exist");
        }
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileService.findByAccount(optionalUser.get());
        if (!optionalEmployeeProfile.isPresent()) {
            throw new ResourceNotFoundException("User is not an employee");
        }
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        if (!business.getId().equals(optionalEmployeeProfile.get().getBusiness().getId())) {
            throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
        }

        Optional<Office> officeOptional = officeService.findByManager(optionalEmployeeProfile.get());
        if(officeOptional.isPresent()){
            officeOptional.get().setManager(null);
            officeService.save(officeOptional.get());
        }

        Optional<EmployeeActivity> employeeActivityOptional = employeeActivityRepository.
                findByEmployeeProfile(optionalEmployeeProfile.get());
        if(employeeActivityOptional.isPresent()){
            throw new ResourceNotFoundException("User is not an employee");
        }

        EmployeeActivity employeeActivity = new EmployeeActivity();
        employeeActivity.setAccount(optionalUser.get());
        employeeActivity.setEmployeeProfile(optionalEmployeeProfile.get());
        employeeActivityRepository.save(employeeActivity);
        return ResponseEntity.ok(new ApiResponse("Employee successfully fired", 200));
    }

    @GetMapping("/offices/{officeId}/employees")
    @Secured({"ROLE_MERCHANT", "ROLE_MANAGER"})
    public List<UserResponse> getEmployees(@CurrentUser UserPrincipal userPrincipal,
                                           @PathVariable Long officeId) {
        Optional<User> optionalUser = userService.findByUsername(userPrincipal.getUsername());
        if (!optionalUser.isPresent()) {
            throw new AppException("Horror");
        }
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileService.findByAccount(optionalUser.get());
        if (!optionalEmployeeProfile.isPresent() || optionalEmployeeProfile.get().getBusiness() == null) {
            throw new AppException("Not an employee");
        }
        List<OfficeProfile> officeProfiles = officeProfileRepository.findAllByOfficeIdAndOffice_BusinessId(
                officeId,
                optionalEmployeeProfile.get().getBusiness().getId()
        );
        List<EmployeeActivity> employeeActivities = employeeActivityRepository.findAll();
        return employeeProfileService
                .findAllByOptionalBusinessId(optionalEmployeeProfile.get().getBusiness().getId())
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

    @PutMapping("/users/{userId}")
    @Secured({"ROLE_ADMIN", "ROLE_MERCHANT", "ROLE_MANAGER"})
    public ResponseEntity<EmployeeProfileResponse> updateUserProfile(@RequestBody EmployeeProfileRequest employeeProfileRequest,
                                                                     @PathVariable Long userId,
                                                                     @CurrentUser UserPrincipal userPrincipal) throws ParseException {
        Optional<User> optionalUser = userService.findUserById(userId);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User with id " + userId + " does not exist");
        }
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileService.findByAccount(optionalUser.get());
        if (!optionalEmployeeProfile.isPresent()) {
            throw new ResourceNotFoundException("User is not an employee");
        }
        if (userPrincipal.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            Optional<User> optionalManager = userService.findByUsername(userPrincipal.getUsername());
            if (!optionalManager.isPresent()) {
                throw new AppException("This shouldn't happen. Contact your administrator!");
            }
            Optional<EmployeeProfile> optionalManagerProfile = employeeProfileService.findByAccount(optionalManager.get());
            if (!optionalManagerProfile.isPresent()) {
                throw new AppException("THIS IS HORROR. KILL THE MACHINE!");
            }
            if (!optionalEmployeeProfile.get().getBusiness().getId().equals(optionalManagerProfile.get().getBusiness().getId())) {
                throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
            }
        }
        EmployeeProfile employeeProfile = optionalEmployeeProfile.get();
        employeeProfile.setName(employeeProfileRequest.getName());
        employeeProfile.setSurname(employeeProfileRequest.getSurname());
        employeeProfile.setDateOfBirth(employeeProfileRequest.getDateFromString());
        employeeProfile.setJmbg(employeeProfileRequest.getJmbg());
        employeeProfile.getContactInformation().setAddress(employeeProfileRequest.getAddress());
        employeeProfile.getContactInformation().setCity(employeeProfileRequest.getCity());
        employeeProfile.getContactInformation().setCountry(employeeProfileRequest.getCountry());
        employeeProfile.getContactInformation().setPhoneNumber(employeeProfileRequest.getPhoneNumber());

        EmployeeProfile result = employeeProfileService.save(employeeProfile);
        return ResponseEntity.ok(new EmployeeProfileResponse(result));
    }

    @PutMapping("/users/roles/{userId}")
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    public ResponseEntity<EmployeeProfileResponse> updateUserRoles(@RequestBody RoleChangeRequest roleChangeRequest,
                                                                   @PathVariable Long userId,
                                                                   @CurrentUser UserPrincipal userPrincipal){
        Optional<User> optionalUser = userService.findUserById(userId);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User with id " + userId + " does not exist");
        }
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileService.findByAccount(optionalUser.get());
        if (!optionalEmployeeProfile.isPresent()) {
            throw new ResourceNotFoundException("User is not an employee");
        }
        if (userPrincipal.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            Optional<User> optionalManager = userService.findByUsername(userPrincipal.getUsername());
            if (!optionalManager.isPresent()) {
                throw new AppException("This shouldn't happen. Contact your administrator!");
            }
            Optional<EmployeeProfile> optionalManagerProfile = employeeProfileService.findByAccount(optionalManager.get());
            if (!optionalManagerProfile.isPresent()) {
                throw new AppException("THIS IS HORROR. KILL THE MACHINE!");
            }
            if (!optionalEmployeeProfile.get().getBusiness().getId().equals(optionalManagerProfile.get().getBusiness().getId())) {
                throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
            }
        }
        List<String> listOfNames = roleChangeRequest.getNewRoles()
                .stream()
                .map(
                        role -> role.getRolename()
                )
                .collect(Collectors.toList());
        if(listOfNames.contains("ROLE_ADMIN")){
            throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
        }
        userService.changeUserRoles(userId,listOfNames);

        return ResponseEntity.ok(new EmployeeProfileResponse(optionalEmployeeProfile.get()));
    }

    @GetMapping("/users/{userId}")
    @Secured({"ROLE_ADMIN", "ROLE_MERCHANT", "ROLE_MANAGER"})
    public ResponseEntity<EmployeeProfileResponse> getUserProfile(@PathVariable Long userId,
                                                                  @CurrentUser UserPrincipal userPrincipal) {
        Optional<User> optionalUser = userService.findUserById(userId);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User with id " + userId + " does not exist");
        }
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileService.findByAccount(optionalUser.get());
        if (!optionalEmployeeProfile.isPresent()) {
            throw new ResourceNotFoundException("User is not an employee");
        }
        if (userPrincipal.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            Optional<User> optionalManager = userService.findByUsername(userPrincipal.getUsername());
            if (!optionalManager.isPresent()) {
                throw new AppException("This shouldn't happen. Contact your administrator!");
            }
            Optional<EmployeeProfile> optionalManagerProfile = employeeProfileService.findByAccount(optionalManager.get());
            if (!optionalManagerProfile.isPresent()) {
                throw new AppException("THIS IS HORROR. KILL THE MACHINE!");
            }
            if (!optionalEmployeeProfile.get().getBusiness().getId().equals(optionalManagerProfile.get().getBusiness().getId())) {
                throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
            }
        }
        EmployeeProfile employeeProfile = optionalEmployeeProfile.get();

        Optional<EmployeeProfile> result = employeeProfileService.findById(employeeProfile.getId());
        if (!result.isPresent()) {
            throw new ResourceNotFoundException("This user is not an employee");
        }
        return ResponseEntity.ok(new EmployeeProfileResponse(result.get()));
    }
}
