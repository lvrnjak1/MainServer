package ba.unsa.etf.si.mainserver.repositories;

import ba.unsa.etf.si.mainserver.models.PDV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PDVRepository extends JpaRepository<PDV, Long> {
    Optional<PDV> findByPdvRate(double pdv_rate);
}
