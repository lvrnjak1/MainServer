package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.auth.OneTimePassword;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.requests.auth.ChangePasswordRequest;
import ba.unsa.etf.si.mainserver.requests.auth.LoginRequest;
import ba.unsa.etf.si.mainserver.requests.auth.RegistrationRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.UserResponse;
import ba.unsa.etf.si.mainserver.responses.auth.LoginResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RegistrationResponse;
import ba.unsa.etf.si.mainserver.responses.auth.RoleResponse;
import ba.unsa.etf.si.mainserver.responses.business.EmployeeProfileResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.OneTimePasswordService;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.text.ParseException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    private final UserService userService;
    private final EmployeeProfileService employeeProfileService;
    private final BusinessService businessService;
    private final OneTimePasswordService oneTimePasswordService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(UserService userService, EmployeeProfileService employeeProfileService,
                                    BusinessService businessService, OneTimePasswordService oneTimePasswordService,
                                    PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.employeeProfileService = employeeProfileService;
        this.businessService = businessService;
        this.oneTimePasswordService = oneTimePasswordService;
        this.passwordEncoder = passwordEncoder;
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
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        registrationRequest.setBusinessId(business.getId());
        userService.checkPermissions(registrationRequest, userPrincipal);
        userService.checkAvailability(registrationRequest);
        userService.checkBusinessPermissions(registrationRequest.getBusinessId(), userPrincipal);
        return evaluateRegistrationAndGetEmployeeProfile(registrationRequest);
    }

    private ResponseEntity<RegistrationResponse> evaluateRegistrationAndGetEmployeeProfile(
            @RequestBody @Valid RegistrationRequest registrationRequest) throws ParseException {
        User result = userService.createUserAccount(registrationRequest);
        EmployeeProfile employeeProfile = employeeProfileService.createEmployeeProfile(registrationRequest, result);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(
                new RegistrationResponse(
                        result.getId(),
                        result.getUsername(),
                        registrationRequest.getPassword(),
                        result.getEmail(),
                        result.getRoles().stream().map(
                                role -> new RoleResponse(role.getId(), role.getName().name())
                        ).collect(Collectors.toList()),
                        new EmployeeProfileResponse(
                                employeeProfile.getId(),
                                employeeProfile.getName(),
                                employeeProfile.getSurname(),
                                employeeProfile.getDateOfBirth(),
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
        System.out.println(loginRequest);
        String jwt = userService.authenticateUser(loginRequest);
        UserResponse userResponse = userService.getUserResponseByUsername(loginRequest.getUsername());
        return ResponseEntity.ok(new LoginResponse(jwt, "Bearer", userResponse));
    }

    @GetMapping("/v2/login")
    @Secured({"ROLE_MERCHANT", "ROLE_MANAGER","ROLE_PRW","ROLE_PRP","ROLE_WAREMAN","ROLE_CASHIER","ROLE_BARTENDER","ROLE_OFFICEMAN"})
    public ApiResponse checkOTP(@CurrentUser UserPrincipal userPrincipal) {
        Optional<User> userOptional = userService.findByUsername(userPrincipal.getUsername());
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("There is no user");
        }
        User user = userOptional.get();
        Optional<OneTimePassword> otpOptional = oneTimePasswordService.findByUser(user);
        if (otpOptional.isPresent()) {
            //mora mijenjati sifru
            //da se slucajno ne moze logovati sa starom
            Random random = new Random();
            String generatedString = random.ints(48, 122 + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(10)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            userService.changeUserPassword(user.getId(), generatedString);
            throw new AppException("Password must be changed!");

        }
        return new ApiResponse("OK", 200);
    }

    @PutMapping("/user/{userId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<RegistrationResponse> changeUserPassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = userService.changeUserPassword(userId, changePasswordRequest.getPassword());
        Optional<OneTimePassword> otpOptional = oneTimePasswordService.findByUser(user);
        if (otpOptional.isPresent()) {
            otpOptional.get().setOneTimePassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
            oneTimePasswordService.save(otpOptional.get());
        } else {
            oneTimePasswordService.createOneTimePassword(user, passwordEncoder.encode(changePasswordRequest.getPassword()));
        }

        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileService.findByAccount(user);
        EmployeeProfile employeeProfile = null;
        employeeProfile = optionalEmployeeProfile.orElseGet(EmployeeProfile::new);
        return ResponseEntity.ok(new RegistrationResponse(
                user.getId(),
                user.getUsername(),
                changePasswordRequest.getPassword(),
                user.getEmail(),
                user.getRoles().stream().map(
                        role -> new RoleResponse(role.getId(), role.getName().name())
                ).collect(Collectors.toList()),
                new EmployeeProfileResponse(
                        employeeProfile.getId(),
                        employeeProfile.getName(),
                        employeeProfile.getSurname(),
                        employeeProfile.getDateOfBirth(),
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
    @Secured({"ROLE_ADMIN","ROLE_MERCHANT", "ROLE_MANAGER","ROLE_PRW","ROLE_PRP","ROLE_WAREMAN","ROLE_CASHIER","ROLE_BARTENDER","ROLE_OFFICEMAN"})
    public ApiResponse changePassword(@CurrentUser UserPrincipal userPrincipal, @RequestBody ChangePasswordRequest changePasswordRequest) {
        Optional<User> userOptional = userService.findByUsername(userPrincipal.getUsername());
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("Error");
        }
        User user = userOptional.get();
        if (userPrincipal.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
        //ako nije admin, onda mora biti upisan u otp tabelu da bi ovo uradio
            Optional<OneTimePassword> otpOptional = oneTimePasswordService.findByUser(user);
            if (otpOptional.isPresent()) {
                oneTimePasswordService.delete(otpOptional.get());
            }
            else{
                throw new UnauthorizedException();
            }
        }
        userService.changeUserPassword(user.getId(),changePasswordRequest.getPassword());
        return new ApiResponse("Password changed!", 200);
    }
}
