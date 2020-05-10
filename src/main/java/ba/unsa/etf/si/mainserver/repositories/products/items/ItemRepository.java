package ba.unsa.etf.si.mainserver.repositories.products.items;

import ba.unsa.etf.si.mainserver.models.products.items.Item;
import ba.unsa.etf.si.mainserver.models.products.items.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByItemType(ItemType itemType);
}
