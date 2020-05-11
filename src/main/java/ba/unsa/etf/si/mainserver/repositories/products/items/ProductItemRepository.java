package ba.unsa.etf.si.mainserver.repositories.products.items;

import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.items.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    Optional<ProductItem> findByProduct_IdAndItem_Id(Long productId, Long itemId);

    List<ProductItem> findAllByProduct(Product product);
}
