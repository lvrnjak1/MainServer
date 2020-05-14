package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.ServerOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerOfficeRepository extends JpaRepository<ServerOffice, Long> {
    Optional<ServerOffice> findByUser(User account);

    Optional<ServerOffice> findByOffice_Id(Long officeId);
}
