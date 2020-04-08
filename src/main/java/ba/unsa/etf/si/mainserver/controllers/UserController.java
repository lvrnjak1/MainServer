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
import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmploymentHistoryRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.business.EmployeeProfileRequest;
import ba.unsa.etf.si.mainserver.requests.business.RoleChangeRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.UserResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RegistrationResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import ba.unsa.etf.si.mainserver.responses.auth.UserNameResponse;
import ba.unsa.etf.si.mainserver.responses.business.EmployeeProfileResponse;
import ba.unsa.etf.si.mainserver.responses.transactions.ReceiptResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.transactions.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
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
    private final BusinessService businessService;
    private final EmploymentHistoryRepository employmentHistoryRepository;
    private final ReceiptService receiptService;

    public UserController(UserService userService, EmployeeProfileService employeeProfileService,
                          OfficeProfileRepository officeProfileRepository,
                          BusinessService businessService,
                          EmploymentHistoryRepository employmentHistoryRepository, ReceiptService receiptService) {
        this.userService = userService;
        this.employeeProfileService = employeeProfileService;
        this.officeProfileRepository = officeProfileRepository;
        this.businessService = businessService;
        this.employmentHistoryRepository = employmentHistoryRepository;
        this.receiptService = receiptService;
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


    @PutMapping("/users/{userId}")
    @Secured({"ROLE_ADMIN", "ROLE_MERCHANT", "ROLE_MANAGER"})
    public ResponseEntity<EmployeeProfileResponse> updateUserProfile(@RequestBody EmployeeProfileRequest employeeProfileRequest,
                                                                     @PathVariable Long userId,
                                                                     @CurrentUser UserPrincipal userPrincipal) throws ParseException {
        User user = userService.findUserById(userId);
        EmployeeProfile employeeProfile2 = employeeProfileService.findEmployeeByAccount(user);
        if (userPrincipal.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            User manager = userService.findUserByUsername(userPrincipal.getUsername());
            EmployeeProfile managerProfile = employeeProfileService.findEmployeeByAccount(manager);
            if (!employeeProfile2.getBusiness().getId().equals(managerProfile.getBusiness().getId())) {
                throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
            }
        }

        EmployeeProfile employeeProfile = employeeProfile2;
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

    void fireEmployee(EmployeeProfile employeeProfile, String role){
        List<OfficeProfile> officeProfiles = officeProfileRepository.findAllByEmployeeId(employeeProfile.getId());
        if(!officeProfiles.isEmpty()){
            for(OfficeProfile officeProfile : officeProfiles){
                if(officeProfile.getOffice().getManager().getId().equals(employeeProfile.getId())){
                    officeProfile.getOffice().setManager(null);
                }
                List<EmploymentHistory> employmentHistoryList =
                        employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole
                                (officeProfile.getEmployee().getId(), officeProfile.getOffice().getId(), role);

                for(EmploymentHistory employmentHistory: employmentHistoryList){
                    if(employmentHistory.getEndDate() == null){
                        employmentHistory.setEndDate(new Date());
                        employmentHistoryRepository.save(employmentHistory);
                        break;
                    }
                }
                officeProfileRepository.delete(officeProfile);
            }
        }
    }

    void setEndDateInEmployment(EmployeeProfile employeeProfile, String role){
        List<EmploymentHistory> employmentHistoryList =
                employmentHistoryRepository.findAllByEmployeeProfileIdAndRole(employeeProfile.getId(), role);
        for(EmploymentHistory employmentHistory: employmentHistoryList){
            if(employmentHistory.getEndDate() == null){
                employmentHistory.setEndDate(new Date());
                employmentHistoryRepository.save(employmentHistory);
                break;
            }
        }
    }

    void makeNewEmloyment(EmployeeProfile employeeProfile, String role) {
        EmploymentHistory employmentHistory = new EmploymentHistory(employeeProfile.getId(), null, new Date(), null, role);
        employmentHistoryRepository.save(employmentHistory);
    }

    @PutMapping("/users/roles/{userId}")
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    public ResponseEntity<EmployeeProfileResponse> updateUserRoles(@RequestBody RoleChangeRequest roleChangeRequest,
                                                                   @PathVariable Long userId,
                                                                   @CurrentUser UserPrincipal userPrincipal){
        User user = userService.findUserById(userId);
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);
        boolean admin = true;
        if (userPrincipal.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            admin = false;
            User manager = userService.findUserByUsername(userPrincipal.getUsername());
            EmployeeProfile managerProfile = employeeProfileService.findEmployeeByAccount(manager);
            if (!employeeProfile.getBusiness().getId().equals(managerProfile.getBusiness().getId())) {
                throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
            }
        }
        List<String> newRoles = roleChangeRequest.getNewRoles()
                .stream()
                .map(
                        role -> role.getRolename()
                )
                .collect(Collectors.toList());
        if(newRoles.contains("ROLE_ADMIN") || newRoles.contains("ROLE_MERCHANT")){
            throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
        }
        List<String> oldRoles = employeeProfile.getAccount().getRoles().stream()
                .map(
                        role -> role.getName().toString()
                )
                .collect(Collectors.toList());
        if(oldRoles.contains("ROLE_ADMIN") || (oldRoles.contains("ROLE_MERCHANT") && !admin)){
            throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
        }
        for (String role : oldRoles) {
            if (newRoles.contains(role)) {
                //sve ok :D ne diraj
            } else if (role.equals("ROLE_PRW") || role.equals("ROLE_WAREMAN") || role.equals("ROLE_PRP") || role.equals("ROLE_MANAGER")) {
                setEndDateInEmployment(employeeProfile, role);
            } else if (role.equals("ROLE_CASHIER") || role.equals("ROLE_BARTENDER") || role.equals("ROLE_OFFICEMAN")) {
                fireEmployee(employeeProfile, role);
            }
        }
        for(String role : newRoles) {
            if (oldRoles.contains(role)) {
                //sve ok :D ne diraj
            } else if (role.equals("ROLE_PRW") || role.equals("ROLE_WAREMAN") || role.equals("ROLE_PRP") || role.equals("ROLE_MANAGER")) {
                makeNewEmloyment(employeeProfile, role);
            } else if (role.equals("ROLE_CASHIER") || role.equals("ROLE_BARTENDER") || role.equals("ROLE_OFFICEMAN")) {
                //nemoj nista jer oni ostaju u ofisu nedodijeljeni
            }
        }
        userService.changeUserRoles(userId,newRoles);
        return ResponseEntity.ok(new EmployeeProfileResponse(employeeProfile));
    }


    @GetMapping("/users/{userId}")
    @Secured({"ROLE_ADMIN", "ROLE_MERCHANT", "ROLE_MANAGER"})
    public ResponseEntity<EmployeeProfileResponse> getUserProfile(@PathVariable Long userId,
                                                                  @CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findUserById(userId);
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);

        if (userPrincipal.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            User manager = userService.findUserByUsername(userPrincipal.getUsername());
            EmployeeProfile managerProfile = employeeProfileService.findEmployeeByAccount(manager);
            if (!employeeProfile.getBusiness().getId().equals(managerProfile.getBusiness().getId())) {
                throw new UnauthorizedException("YOU DO NOT HAVE THE PERMISSION TO DO THIS");
            }
        }

        EmployeeProfile result = employeeProfileService.findEmployeeById(employeeProfile.getId());
        return ResponseEntity.ok(new EmployeeProfileResponse(result));
    }

    @GetMapping("/users/{userId}/username")
    @Secured({"ROLE_MANAGER"})
    public UserNameResponse getUsernameForId(@PathVariable Long userId,
                                             @CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findUserById(userId);
        EmployeeProfile employeeProfile =  employeeProfileService.findEmployeeByAccount(user);
        return new UserNameResponse(user.getUsername(),
                employeeProfile.getName(), employeeProfile.getSurname());
    }

    @GetMapping("/users/{username}/receipts")
    @Secured("ROLE_MANAGER")
    public List<ReceiptResponse> getReceiptsForUsername(@CurrentUser UserPrincipal userPrincipal,
                                                        @PathVariable String username){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        User user = userService.findUserByUsername(username);
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);

        if(!employeeProfile.getBusiness().getId().equals(business.getId())){
            throw new ResourceNotFoundException("Username doesn't belong to any employee of your business");
        }

        return receiptService.findAllByUsername(username)
                .stream()
                .map(ReceiptResponse::new)
                .collect(Collectors.toList());
    }
}
