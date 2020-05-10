package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.items.Item;
import ba.unsa.etf.si.mainserver.models.products.items.ItemType;
import ba.unsa.etf.si.mainserver.repositories.products.ProductRepository;
import ba.unsa.etf.si.mainserver.repositories.products.items.ItemRepository;
import ba.unsa.etf.si.mainserver.repositories.products.items.ItemTypeRepository;
import ba.unsa.etf.si.mainserver.repositories.products.items.ProductItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final ProductItemRepository productItemRepository;
    private final ProductRepository productRepository;


    public ItemService(ItemRepository itemRepository,
                       ItemTypeRepository itemTypeRepository,
                       ProductItemRepository productItemRepository,
                       ProductRepository productRepository) {
        this.itemRepository = itemRepository;
        this.itemTypeRepository = itemTypeRepository;
        this.productItemRepository = productItemRepository;
        this.productRepository = productRepository;
    }

    public List<ItemType> findAllItemTypesByBusiness(Business business) {
        return itemTypeRepository.findAllByBusinessId(business.getId());
    }

    public Optional<ItemType> findItemTypeByName(String name) {
        return itemTypeRepository.findByName(name);
    }

    public ItemType save(ItemType itemType) {
        return itemTypeRepository.save(itemType);
    }

    public ItemType findById(Long typeId) {
        return itemTypeRepository
                .findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Type doesn't exist"));
    }

    public ItemType findByIdAndBusinessId(Long typeId, Long businessId) {
        ItemType itemType = findById(typeId);
        if(!itemType.getBusinessId().equals(businessId)){
            throw new ResourceNotFoundException("Type doesn't exist");
        }

        return itemType;
    }

    public void delete(ItemType itemType) {
        productRepository.findAllByItemType(itemType)
        .forEach(product -> product.setItemType(null));
        itemTypeRepository.delete(itemType);
    }

    public List<Item> getItemsByType(ItemType itemType) {
        return itemRepository.findAllByItemType(itemType);
    }
}
