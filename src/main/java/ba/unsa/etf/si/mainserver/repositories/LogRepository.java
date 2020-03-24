package ba.unsa.etf.si.mainserver.repositories;

import ba.unsa.etf.si.mainserver.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
