package ba.unsa.etf.si.mainserver.repositories.products.items;

import ba.unsa.etf.si.mainserver.models.products.items.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
}
