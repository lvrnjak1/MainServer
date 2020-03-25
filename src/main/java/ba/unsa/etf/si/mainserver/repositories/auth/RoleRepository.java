package ba.unsa.etf.si.mainserver.repositories.auth;

import ba.unsa.etf.si.mainserver.models.auth.Role;
import ba.unsa.etf.si.mainserver.models.auth.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
