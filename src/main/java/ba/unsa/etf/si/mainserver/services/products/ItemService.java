package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.items.Item;
import ba.unsa.etf.si.mainserver.models.products.items.ItemType;
import ba.unsa.etf.si.mainserver.models.products.items.ProductItem;
import ba.unsa.etf.si.mainserver.repositories.products.ProductRepository;
import ba.unsa.etf.si.mainserver.repositories.products.items.ItemRepository;
import ba.unsa.etf.si.mainserver.repositories.products.items.ItemTypeRepository;
import ba.unsa.etf.si.mainserver.repositories.products.items.ProductItemRepository;
import ba.unsa.etf.si.mainserver.responses.products.items.ItemResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public  List<ProductItem> findAllProductItems(Product product) {
        return productItemRepository.findAllByProduct(product);
                //.stream()
                //.map(productItem -> productItem.getItem())
                //.collect(Collectors.toList());
    }

    public List<ItemType> findAllItemTypesByBusiness(Business business) {
        return itemTypeRepository.findAllByBusinessId(business.getId());
    }

    public Optional<ItemType> findItemTypeByName(String name) {
        return itemTypeRepository.findByName(name);
    }

    public ItemType saveItemType(ItemType itemType) {
        return itemTypeRepository.save(itemType);
    }

    public ItemType findItemTypeById(Long typeId) {
        return itemTypeRepository
                .findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Type doesn't exist"));
    }

    public ItemType findItemTypeByIdAndBusinessId(Long typeId, Long businessId) {
        ItemType itemType = findItemTypeById(typeId);
        if(!itemType.getBusinessId().equals(businessId)){
            throw new ResourceNotFoundException("Type doesn't exist");
        }

        return itemType;
    }

    public void deleteItemType(ItemType itemType) {
        productRepository.findAllByItemType(itemType)
        .forEach(product -> product.setItemType(null));
        //itemRepository.findAllByItemType(itemType).forEach(itemRepository::delete);
        itemTypeRepository.delete(itemType);
    }

    public List<Item> getItemsByType(ItemType itemType) {
        return itemRepository.findAllByItemType(itemType);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Item findItemByIdAndBusiness(Long itemId, Long businessId) {
        return itemRepository.findByIdAndItemType_BusinessId(itemId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Item doesn't exist"));
    }

    public void deleteItem(Item item) {
        itemRepository.delete(item);
    }

    public ProductItem findProductItem(Long productId, Long itemId) {
        return productItemRepository.findByProduct_IdAndItem_Id(productId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item doesn't exist for this product"));
    }

    public void deleteProductItem(ProductItem productItem) {
        productItemRepository.delete(productItem);
    }

    public ProductItem saveProductItem(ProductItem productItem) {
        return productItemRepository.save(productItem);
    }

    public List<ProductItem> findProductItemsByProduct(Product product) {
        return productItemRepository.findAllByProduct(product);
    }

    public Item findItemByIdAndBusinessAndItemType(Long itemId, Long businessId, ItemType itemType) {
        return itemRepository.findByIdAndItemType_BusinessIdAndItemType_Id(itemId, businessId, itemType.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Item doesn't exist"));
    }

    public List<ItemResponse> getAllItemsForBusiness(Business business) {
        return itemRepository.findAllByItemType_BusinessId(business.getId())
                .stream()
                .map(ItemResponse::new)
                .collect(Collectors.toList());
    }
}
