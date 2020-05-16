package ba.unsa.etf.si.mainserver.repositories.products;

import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.items.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBusiness(Business business);

    List<Product> findAllByBusinessId(Long businessId);

    Optional<Product> findById(Long id);

    List<Product> findAllByItemType(ItemType itemType);

    List<Product> findAllByItemType_Id(Long typeId);
}
