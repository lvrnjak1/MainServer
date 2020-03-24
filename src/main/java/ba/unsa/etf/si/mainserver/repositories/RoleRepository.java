package ba.unsa.etf.si.mainserver.repositories;

import ba.unsa.etf.si.mainserver.models.Role;
import ba.unsa.etf.si.mainserver.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
