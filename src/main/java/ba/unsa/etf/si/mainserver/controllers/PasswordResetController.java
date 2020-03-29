package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.auth.PasswordResetToken;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.requests.auth.PasswordResetRequest;
import ba.unsa.etf.si.mainserver.requests.auth.SaveNewPasswordRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.services.EmailService;
import ba.unsa.etf.si.mainserver.services.PasswordTokenService;
import ba.unsa.etf.si.mainserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PasswordResetController {
    @Autowired
    private EmailService emailService;
    private final PasswordTokenService passwordTokenService;
    private final UserService userService;

    public PasswordResetController(PasswordTokenService passwordTokenService, UserService userService) {
        this.passwordTokenService = passwordTokenService;
        this.userService = userService;
    }

    @PostMapping("/user/resetPassword")
    public ApiResponse resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        Optional<User> userOptional = userService.findByEmail(passwordResetRequest.getEmail());
        if (!userOptional.isPresent()) {
            throw new AppException("User with email " + passwordResetRequest.getEmail() + " doesn't exist");
        }
        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
        passwordResetEmail.setTo(user.getEmail());
        passwordResetEmail.setSubject("Password Reset Request");
        passwordResetEmail.setText("To reset your password, use token below:\n" + token);

        emailService.sendEmail(passwordResetEmail);
        return new ApiResponse("E-mail successfully sent", 200);
    }

    @PostMapping("/user/savePassword")
    public ApiResponse savePassword(@RequestBody SaveNewPasswordRequest saveNewPasswordRequest) {
        String token = saveNewPasswordRequest.getToken();
        String newPassword = saveNewPasswordRequest.getNewPassword();
        User user = userService.findUserByEmailToken(token);

        Optional<PasswordResetToken> passToken = passwordTokenService.findByToken(token);

        Calendar cal = Calendar.getInstance();
        if ((passToken.get().getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            throw new AppException("Expired token");
        }

        userService.changeUserPassword(user.getId(),newPassword);
        passwordTokenService.deletePasswordResetToken(passToken.get().getId());

        return new ApiResponse("Password successfully changed", 200);

    }

}

