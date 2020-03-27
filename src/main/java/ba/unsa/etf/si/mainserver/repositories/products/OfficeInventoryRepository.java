package ba.unsa.etf.si.mainserver.repositories.products;

import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import ba.unsa.etf.si.mainserver.models.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfficeInventoryRepository extends JpaRepository<OfficeInventory, Long> {
    List<OfficeInventory> findAllByOffice(Office office);

    Optional<OfficeInventory> findByProductAndOffice(Product product, Office office);

    List<OfficeInventory> findAllByProduct(Product product);
}
