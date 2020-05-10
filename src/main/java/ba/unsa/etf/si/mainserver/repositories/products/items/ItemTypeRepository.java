package ba.unsa.etf.si.mainserver.repositories.products.items;

import ba.unsa.etf.si.mainserver.models.products.items.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {
    List<ItemType> findAllByBusinessId(Long businessId);

    Optional<ItemType> findByName(String name);
}
