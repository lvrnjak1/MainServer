package ba.unsa.etf.si.mainserver.services;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.Role;
import ba.unsa.etf.si.mainserver.models.RoleName;
import ba.unsa.etf.si.mainserver.models.User;
import ba.unsa.etf.si.mainserver.repositories.RoleRepository;
import ba.unsa.etf.si.mainserver.repositories.UserRepository;
import ba.unsa.etf.si.mainserver.requests.LoginRequest;
import ba.unsa.etf.si.mainserver.requests.RegistrationRequest;
import ba.unsa.etf.si.mainserver.security.JwtTokenProvider;
import ba.unsa.etf.si.mainserver.security.UserCreationPermissions;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;


    public UserService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       RoleRepository roleRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
    }

    public void checkPermissions(RegistrationRequest registrationRequest, UserPrincipal userPrincipal) {
        if (!registrationRequest
                .getRoles()
                .stream()
                .allMatch(
                        role -> userPrincipal
                                .getAuthorities()
                                .stream()
                                .anyMatch(authority ->
                                        UserCreationPermissions
                                                .permissions
                                                .get(authority.toString()).contains(role.getRolename()))
                )) {
            throw new UnauthorizedException("You do not have such permission!");
        }
    }

    public void checkAvailability(RegistrationRequest registrationRequest) {
        if(userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new AppException("Username already taken");
        }

        if(userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new AppException("Email Address already in use!");
        }
    }

    public User createUserAccount(RegistrationRequest registrationRequest) {
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

        return userRepository.save(user);
    }

    public String authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        if(!authentication.getAuthorities().stream().anyMatch(authority -> authority.toString().equals(loginRequest.getRole()))){
            throw new UnauthorizedException("You do not have such permissions!");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }
}
