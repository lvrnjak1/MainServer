package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.Role;
import ba.unsa.etf.si.mainserver.models.RoleName;
import ba.unsa.etf.si.mainserver.models.User;
import ba.unsa.etf.si.mainserver.repositories.RoleRepository;
import ba.unsa.etf.si.mainserver.repositories.UserRepository;
import ba.unsa.etf.si.mainserver.requests.LoginRequest;
import ba.unsa.etf.si.mainserver.requests.RegistrationRequest;
import ba.unsa.etf.si.mainserver.responses.LoginResponse;
import ba.unsa.etf.si.mainserver.responses.RegistrationResponse;
import ba.unsa.etf.si.mainserver.responses.RoleResponse;
import ba.unsa.etf.si.mainserver.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthenticationController(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RoleRepository roleRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        if(userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new AppException("Username already taken");
        }

        if(userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new AppException("Email Address already in use!");
        }

        // Creating user's account
        User user = new User(null, registrationRequest.getUsername(), registrationRequest.getPassword(),
                registrationRequest.getEmail(), null);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        List<Role> userRoles;
        try {
            userRoles = registrationRequest
                    .getRoles()
                    .stream()
                    .map(
                            rolename ->
                                    roleRepository
                                            .findByName(Enum.valueOf(RoleName.class,rolename.getRolename()))
                                            .orElseThrow(() -> new AppException("User role not set!"))
                    ).collect(Collectors.toList());
        } catch (Exception e) {
            throw new AppException("No such user role!");
        }

        user.setRoles(new HashSet<>(userRoles));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(
                new RegistrationResponse(user.getId(),
                        user.getUsername(),
                        registrationRequest.getPassword(),
                        user.getEmail(),
                        user.getRoles().stream().map(
                                role -> new RoleResponse(role.getName().name()))
                                .collect(Collectors.toList())));

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new LoginResponse(jwt,"Bearer"));
    }


}
