package ba.unsa.etf.si.mainserver.repositories;

import ba.unsa.etf.si.mainserver.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);
}
