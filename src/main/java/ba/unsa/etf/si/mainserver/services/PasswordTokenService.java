package ba.unsa.etf.si.mainserver.services;

import ba.unsa.etf.si.mainserver.models.auth.PasswordResetToken;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.repositories.auth.PasswordTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordTokenService {
    private final PasswordTokenRepository passwordTokenRepository;

    public PasswordTokenService(PasswordTokenRepository passwordTokenRepository) {
        this.passwordTokenRepository = passwordTokenRepository;
    }

    public Optional<PasswordResetToken> findByToken(String token) {
        return passwordTokenRepository.findByToken(token);
    }

}
