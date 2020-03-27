package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.business.EmployeeProfileRequest;
import ba.unsa.etf.si.mainserver.responses.UserResponse;
import ba.unsa.etf.si.mainserver.responses.business.EmployeeProfileResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final EmployeeProfileService employeeProfileService;
    private final OfficeProfileRepository officeProfileRepository;


    public UserController(UserService userService, EmployeeProfileService employeeProfileService, OfficeProfileRepository officeProfileRepository) {
        this.userService = userService;
        this.employeeProfileService = employeeProfileService;
        this.officeProfileRepository = officeProfileRepository;
    }

    @GetMapping("/users")
    @Secured("ROLE_ADMIN")
    public List<UserResponse> getUsers(@RequestParam(required = false) Long businessId) {
        return employeeProfileService
                .findAllByOptionalBusinessId(businessId)
                .stream()
                .map(
                        employeeProfile -> new UserResponse(
                                employeeProfile.getAccount().getId(),
                                employeeProfile.getAccount().getUsername(),
                                employeeProfile.getAccount().getEmail(),
                                employeeProfile.getName(),
                                employeeProfile.getSurname(),
                                employeeProfile.getContactInformation().getAddress(),
                                employeeProfile.getContactInformation().getPhoneNumber(),
                                employeeProfile.getContactInformation().getCountry(),
                                employeeProfile.getContactInformation().getCity())
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
                                employeeProfile.getContactInformation().getAddress(),
                                employeeProfile.getContactInformation().getPhoneNumber(),
                                employeeProfile.getContactInformation().getCountry(),
                                employeeProfile.getContactInformation().getCity())
                )
                .collect(Collectors.toList());
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
                                employeeProfile.getContactInformation().getAddress(),
                                employeeProfile.getContactInformation().getPhoneNumber(),
                                employeeProfile.getContactInformation().getCountry(),
                                employeeProfile.getContactInformation().getCity())
                )
                .collect(Collectors.toList());
    }

    @PutMapping("/users/{userId}")
    @Secured({"ROLE_ADMIN", "ROLE_MERCHANT", "ROLE_MANAGER"})
    public ResponseEntity<EmployeeProfileResponse> updateUserProfile(@RequestBody EmployeeProfileRequest employeeProfileRequest,
                                                                     @PathVariable Long userId,
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
        employeeProfile.setName(employeeProfileRequest.getName());
        employeeProfile.setSurname(employeeProfileRequest.getSurname());
        employeeProfile.getContactInformation().setAddress(employeeProfileRequest.getAddress());
        employeeProfile.getContactInformation().setCity(employeeProfileRequest.getCity());
        employeeProfile.getContactInformation().setCountry(employeeProfileRequest.getCountry());
        employeeProfile.getContactInformation().setPhoneNumber(employeeProfileRequest.getPhoneNumber());

        EmployeeProfile result = employeeProfileService.save(employeeProfile);
        return ResponseEntity.ok(new EmployeeProfileResponse(result));
    }


}
