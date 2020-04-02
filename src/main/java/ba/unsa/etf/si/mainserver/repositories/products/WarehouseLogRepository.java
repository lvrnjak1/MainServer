package ba.unsa.etf.si.mainserver.repositories.products;

import ba.unsa.etf.si.mainserver.models.products.WarehouseLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseLogRepository  extends JpaRepository<WarehouseLog, Long> {
}
