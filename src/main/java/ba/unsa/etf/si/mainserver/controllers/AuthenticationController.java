package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.configurations.Actions;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import ba.unsa.etf.si.mainserver.repositories.business.EmploymentHistoryRepository;
import ba.unsa.etf.si.mainserver.requests.auth.ChangePasswordRequest;
import ba.unsa.etf.si.mainserver.requests.auth.LoginRequest;
import ba.unsa.etf.si.mainserver.requests.auth.RegistrationRequest;
import ba.unsa.etf.si.mainserver.requests.auth.SyncPasswordRequest;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationPayload;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.UserResponse;
import ba.unsa.etf.si.mainserver.responses.auth.LoginResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RegistrationResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import ba.unsa.etf.si.mainserver.responses.business.EmployeeProfileResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    private final UserService userService;
    private final EmployeeProfileService employeeProfileService;
    private final BusinessService businessService;
    private final EmploymentHistoryRepository employmentHistoryRepository;
    private final LogServerService logServerService;

    public AuthenticationController(UserService userService, EmployeeProfileService employeeProfileService,
                                    BusinessService businessService,
                                    EmploymentHistoryRepository employmentHistoryRepository,
                                    LogServerService logServerService) {
        this.userService = userService;
        this.employeeProfileService = employeeProfileService;
        this.businessService = businessService;
        this.employmentHistoryRepository = employmentHistoryRepository;
        this.logServerService = logServerService;
    }

    @PostMapping("/_register")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<RegistrationResponse> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest,
            @CurrentUser UserPrincipal userPrincipal) throws ParseException {
        userService.checkPermissions(registrationRequest, userPrincipal);
        userService.checkAvailability(registrationRequest);
        return evaluateRegistrationAndGetEmployeeProfile(registrationRequest);
    }

    @PostMapping("/register")
    @Secured({"ROLE_MERCHANT", "ROLE_MANAGER"})
    public ResponseEntity<RegistrationResponse> registerEmployee(
            @Valid @RequestBody RegistrationRequest registrationRequest,
            @CurrentUser UserPrincipal userPrincipal) throws ParseException {
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        registrationRequest.setBusinessId(business.getId());
        userService.checkPermissions(registrationRequest, userPrincipal);
        userService.checkAvailability(registrationRequest);
        userService.checkBusinessPermissions(registrationRequest.getBusinessId(), userPrincipal);
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                userPrincipal.getUsername(),
                                "user_create",
                                "User " + userPrincipal.getUsername() + " has created an account with username " + registrationRequest.getUsername() + "!"
                        )
                )
                ,
                "admin"
        );
        return evaluateRegistrationAndGetEmployeeProfile(registrationRequest);
    }

    private ResponseEntity<RegistrationResponse> evaluateRegistrationAndGetEmployeeProfile(
            @RequestBody @Valid RegistrationRequest registrationRequest) throws ParseException {
        User result = userService.createUserAccount(registrationRequest);
        EmployeeProfile employeeProfile = employeeProfileService.createEmployeeProfile(registrationRequest, result);
        registrationRequest.getRoles().stream()
                .map(RoleResponse::getRolename)
                .filter(role -> role.equals("ROLE_PRW") || role.equals("ROLE_MANAGER") || role.equals("ROLE_WAREMAN") || role.equals("ROLE_PRP"))
                .forEach(role -> employmentHistoryRepository.save(new EmploymentHistory(employeeProfile.getId(),null,new Date(),null,role)));

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                result.getUsername(),
                                "assign_employee",
                                employeeProfile.getName() + " " + employeeProfile.getSurname() + " has been assigned."
                        )
                ),
                "merchant_dashboard"
        );
        return ResponseEntity.created(location).body(
                new RegistrationResponse(
                        result.getId(),
                        result.getUsername(),
                        registrationRequest.getPassword(),
                        result.getEmail(),
                        result.isOtp(),
                        result.getRoles().stream().map(
                                role -> new RoleResponse(role.getId(), role.getName().name())
                        ).collect(Collectors.toList()),
                        new EmployeeProfileResponse(
                                employeeProfile.getId(),
                                employeeProfile.getName(),
                                employeeProfile.getSurname(),
                                employeeProfile.getStringDate(),
                                employeeProfile.getJmbg(),
                                employeeProfile.getContactInformation().getAddress(),
                                employeeProfile.getContactInformation().getCity(),
                                employeeProfile.getContactInformation().getCountry(),
                                employeeProfile.getContactInformation().getEmail(),
                                employeeProfile.getContactInformation().getPhoneNumber(),
                                employeeProfile.getAccount()
                        )
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = userService.authenticateUser(loginRequest);
        UserResponse userResponse = userService.getUserResponseByUsername(loginRequest.getUsername());
        logAndNotify(loginRequest);
        return ResponseEntity.ok(new LoginResponse(jwt, "Bearer", userResponse));
    }

    @PostMapping("/v2/login")
    public ResponseEntity<?> authenticateUserV2(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = userService.authenticateUser(loginRequest);
        UserResponse userResponse = userService.getUserResponseByUsername(loginRequest.getUsername());

        User user = userService.findUserByUsername(loginRequest.getUsername());
        if(user.isOtp()){
            Random random = new Random();
            String generatedString = random.ints(48, 122 + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(10)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            userService.changeUserPassword(user.getId(), generatedString);
        }
        logAndNotify(loginRequest);
        return ResponseEntity.ok(new LoginResponse(jwt, "Bearer", userResponse));
    }

    private void logAndNotify(@RequestBody @Valid LoginRequest loginRequest) {
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                loginRequest.getUsername(),
                Actions.LOGIN_ACTION_NAME,
                "system",
                "User " + loginRequest.getUsername() + " has logged into the system!"
        );
        if (loginRequest.getRole().equals("ROLE_MERCHANT")) {
            logServerService.broadcastNotification(
                    new NotificationRequest(
                            "info",
                            new NotificationPayload(
                                    loginRequest.getUsername(),
                                    "login",
                                    "User " + loginRequest.getUsername() + " has logged into the Merchant Dashboard Web app."
                            )
                    ),
                    "user_management"
            );
        }
    }

    @PutMapping("/user/{userId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<RegistrationResponse> changeUserPassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordRequest changePasswordRequest,
            @CurrentUser UserPrincipal userPrincipal) {
        User user = userService.changeUserPassword(userId, changePasswordRequest.getPassword());
        user.setOtp(true);
        userService.save(user);
        EmployeeProfile employeeProfile = new EmployeeProfile();
        try {
            employeeProfile = employeeProfileService.findEmployeeByAccount(user);
        } catch (Exception ignored) {
        }
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.ADMIN_PASSWORD_CHANGE_ACTION_NAME,
                "user_account",
                "Admin " + userPrincipal.getUsername() + " has changed password of " + user.getUsername() + "!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return ResponseEntity.ok(new RegistrationResponse(
                user.getId(),
                user.getUsername(),
                changePasswordRequest.getPassword(),
                user.getEmail(),
                user.isOtp(),
                user.getRoles().stream().map(
                        role -> new RoleResponse(role.getId(), role.getName().name())
                ).collect(Collectors.toList()),
                new EmployeeProfileResponse(
                        employeeProfile.getId(),
                        employeeProfile.getName(),
                        employeeProfile.getSurname(),
                        employeeProfile.getStringDate(),
                        employeeProfile.getJmbg(),
                        employeeProfile.getContactInformation() != null ? employeeProfile.getContactInformation().getAddress() : null,
                        employeeProfile.getContactInformation() != null ? employeeProfile.getContactInformation().getCity() : null,
                        employeeProfile.getContactInformation() != null ? employeeProfile.getContactInformation().getCountry() : null,
                        employeeProfile.getContactInformation() != null ? employeeProfile.getContactInformation().getEmail() : null,
                        employeeProfile.getContactInformation() != null ? employeeProfile.getContactInformation().getPhoneNumber() : null,
                        employeeProfile.getAccount()
                )));
    }

    @PutMapping("/changePassword")
    @Secured({"ROLE_ADMIN","ROLE_MERCHANT","ROLE_MANAGER","ROLE_PRW","ROLE_PRP","ROLE_WAREMAN","ROLE_CASHIER","ROLE_BARTENDER","ROLE_OFFICEMAN"})
    public ApiResponse changePassword(@CurrentUser UserPrincipal userPrincipal,
                                      @RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = userService.findUserByUsername(userPrincipal.getUsername());
        if (userPrincipal.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            if (!user.isOtp()) {
                throw new UnauthorizedException("You do not have permission to change password");
            }
        }
        user.setOtp(false);
        userService.save(user);
        userService.changeUserPassword(user.getId(), changePasswordRequest.getPassword());
        logAndNotifyPasswordChange(userPrincipal.getUsername());
        return new ApiResponse("Password changed!", 200);
    }

    @PutMapping("/office-changePassword")
    @Secured({"ROLE_SERVER"})
    public ApiResponse officeChangePassword(@RequestBody SyncPasswordRequest syncPasswordRequest) {
        User user = userService.findUserByUsername(syncPasswordRequest.getUsername());
        user.setOtp(false);
        //oni posalju zakodiran string
        user.setPassword(syncPasswordRequest.getPassword());
        userService.save(user);
        logAndNotifyPasswordChange(syncPasswordRequest.getUsername());
        return new ApiResponse("Password changed!", 200);
    }

    private void logAndNotifyPasswordChange(String username){
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                username,
                Actions.PASSWORD_CHANGE_ACTION_NAME,
                "user_acount",
                "User " + username + " has changed his password!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "warning",
                        new NotificationPayload(
                                username,
                                "password_change",
                                username + " has changed his/her password."
                        )
                ),
                "user_management"
        );
    }
}
