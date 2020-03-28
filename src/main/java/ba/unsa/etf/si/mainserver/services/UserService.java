package ba.unsa.etf.si.mainserver.services;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.auth.Role;
import ba.unsa.etf.si.mainserver.models.auth.RoleName;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.auth.RoleRepository;
import ba.unsa.etf.si.mainserver.repositories.auth.UserRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.auth.LoginRequest;
import ba.unsa.etf.si.mainserver.requests.auth.RegistrationRequest;
import ba.unsa.etf.si.mainserver.responses.UserResponse;
import ba.unsa.etf.si.mainserver.security.JwtTokenProvider;
import ba.unsa.etf.si.mainserver.security.UserCreationPermissions;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmployeeProfileRepository employeeProfileRepository;


    public UserService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       RoleRepository roleRepository, EmployeeProfileRepository employeeProfileRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.employeeProfileRepository = employeeProfileRepository;
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
        System.out.println("Proso");

        authentication.getAuthorities().stream().forEach(authority -> System.out.println(authority.getAuthority()));
        if(!authentication.getAuthorities().stream().anyMatch(authority -> authority.toString().equals(loginRequest.getRole()))){
            throw new UnauthorizedException("You do not have such permissions!");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public User changeUserRoles(Long userId, List<String> roles) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user
                    .setRoles(
                            roles
                                    .stream()
                                    .map(
                                            rolename -> {
                                                return roleRepository.findByName(Enum.valueOf(RoleName.class,rolename)).get();
                                            }
                                            )
                                    .collect(Collectors.toSet()));
            return userRepository.save(user);

        }
        throw new ResourceNotFoundException("User with id " + userId + " not found!");
    }

    public void checkBusinessPermissions(Long businessId, UserPrincipal userPrincipal) {
        Optional<User> optionalUser = userRepository.findByUsername(userPrincipal.getUsername());
        if (optionalUser.isPresent()) {
            Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileRepository.findByAccountId(optionalUser.get().getId());
            if (optionalEmployeeProfile.isPresent()) {
                EmployeeProfile employeeProfile = optionalEmployeeProfile.get();
                if (employeeProfile.getBusiness() != null) {
                    if (employeeProfile.getBusiness().getId().equals(businessId)) {
                        return;
                    }
                }
            }
        }
        throw new UnauthorizedException("You do not have the necessary permissions!");
    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public UserResponse getUserResponseByUsername(String username) {
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileRepository.findByContactInformation_EmailOrAccount_UsernameOrAccount_IdOrId("", username, 0L, 0L);
        if (!optionalEmployeeProfile.isPresent()) {
            return new UserResponse();
        }
        EmployeeProfile employeeProfile = optionalEmployeeProfile.get();
        return new UserResponse(
                employeeProfile.getAccount().getId(),
                username,
                employeeProfile.getContactInformation().getEmail(),
                employeeProfile.getName(),
                employeeProfile.getSurname(),
                employeeProfile.getContactInformation().getAddress(),
                employeeProfile.getContactInformation().getPhoneNumber(),
                employeeProfile.getContactInformation().getCountry(),
                employeeProfile.getContactInformation().getCity()
        );
    }
}
