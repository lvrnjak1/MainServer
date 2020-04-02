package ba.unsa.etf.si.mainserver.repositories.products;

import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.WarehouseLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseLogRepository  extends JpaRepository<WarehouseLog, Long> {
    List<WarehouseLog> findAllByProduct_Business(Business business);
}
