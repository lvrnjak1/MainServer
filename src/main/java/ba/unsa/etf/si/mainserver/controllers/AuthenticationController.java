package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.models.User;
import ba.unsa.etf.si.mainserver.requests.LoginRequest;
import ba.unsa.etf.si.mainserver.requests.RegistrationRequest;
import ba.unsa.etf.si.mainserver.responses.LoginResponse;
import ba.unsa.etf.si.mainserver.responses.RegistrationResponse;
import ba.unsa.etf.si.mainserver.responses.RoleResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest,
            @CurrentUser UserPrincipal userPrincipal) {
        userService.checkPermissions(registrationRequest,userPrincipal);
        userService.checkAvailability(registrationRequest);

        User result = userService.createUserAccount(registrationRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(
                new RegistrationResponse(result.getId(),
                        result.getUsername(),
                        registrationRequest.getPassword(),
                        result.getEmail(),
                        result.getRoles().stream().map(
                                role -> new RoleResponse(role.getName().name()))
                                .collect(Collectors.toList())));

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new LoginResponse(jwt,"Bearer"));
    }

}
