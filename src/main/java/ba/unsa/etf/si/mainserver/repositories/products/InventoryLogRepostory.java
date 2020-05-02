package ba.unsa.etf.si.mainserver.repositories.products;

import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.InventoryLog;
import ba.unsa.etf.si.mainserver.models.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryLogRepostory extends JpaRepository<InventoryLog, Long> {
    List<InventoryLog> findAllByProduct_Business(Business business);
    List<InventoryLog> findAllByProduct(Product product);
}
