package ba.unsa.etf.si.mainserver.repositories.auth;

import ba.unsa.etf.si.mainserver.models.auth.OneTimePassword;
import ba.unsa.etf.si.mainserver.models.auth.PasswordResetToken;
import ba.unsa.etf.si.mainserver.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword, Long> {
    Optional<OneTimePassword> findByUser(User user);
}
