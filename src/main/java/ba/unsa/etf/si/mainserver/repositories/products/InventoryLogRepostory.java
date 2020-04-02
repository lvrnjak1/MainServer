package ba.unsa.etf.si.mainserver.repositories.products;

import ba.unsa.etf.si.mainserver.models.products.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLogRepostory extends JpaRepository<InventoryLog, Long> {
}
