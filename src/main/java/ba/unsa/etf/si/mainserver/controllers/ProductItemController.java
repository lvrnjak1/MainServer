package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.items.Item;
import ba.unsa.etf.si.mainserver.models.products.items.ItemType;
import ba.unsa.etf.si.mainserver.models.products.items.ProductItem;
import ba.unsa.etf.si.mainserver.requests.products.items.ItemRequest;
import ba.unsa.etf.si.mainserver.requests.products.items.ItemTypeRequest;
import ba.unsa.etf.si.mainserver.requests.products.items.ProductItemRequest;
import ba.unsa.etf.si.mainserver.requests.products.items.ProductItemTypeRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.products.ProductItemResponse;
import ba.unsa.etf.si.mainserver.responses.products.items.ItemResponse;
import ba.unsa.etf.si.mainserver.responses.products.items.ItemTypeResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.products.ItemService;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductItemController {
    private final BusinessService businessService;
    private final ItemService itemService;
    private final ProductService productService;

    public ProductItemController(BusinessService businessService,
                                 ItemService itemService,
                                 ProductService productService) {
        this.businessService = businessService;
        this.itemService = itemService;
        this.productService = productService;
    }

    //ruta da se dobiju svi item typeovi
    @GetMapping("/itemtypes")
    @Secured({"ROLE_WAREMAN", "ROLE_OFFICEMAN"})
    public List<ItemTypeResponse> getAllItemTypes(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return getAllItemTypeResponsesByBusiness(business);
    }

    //ruta da se doda novi item type
    @PostMapping("/itemtypes")
    @Secured("ROLE_WAREMAN")
    public List<ItemTypeResponse> addItemType(@CurrentUser UserPrincipal userPrincipal,
                                              @RequestBody ItemTypeRequest itemTypeRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);

        if(itemService.findItemTypeByName(itemTypeRequest.getName()).isPresent()){
            throw new AppException("Item type already exists");
        }

        ItemType itemType = new ItemType(itemTypeRequest.getName(), business.getId());
        itemService.saveItemType(itemType);
        return getAllItemTypeResponsesByBusiness(business);
    }

    private List<ItemTypeResponse> getAllItemTypeResponsesByBusiness(Business business){
        return itemService.findAllItemTypesByBusiness(business)
                .stream()
                .map(ItemTypeResponse::new)
                .collect(Collectors.toList());
    }

    //ruta da se obrise item type
    @DeleteMapping("/itemtypes/{typeId}")
    @Secured("ROLE_WAREMAN")
    public List<ItemTypeResponse> deleteItemType(@CurrentUser UserPrincipal userPrincipal,
                                                 @PathVariable Long typeId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        ItemType itemType = itemService.findItemTypeByIdAndBusinessId(typeId, business.getId());
        itemService.deleteItemType(itemType);
        return getAllItemTypeResponsesByBusiness(business);
    }

    //ruta da se dobiju svi itemovi po type-u
    @GetMapping("/itemtypes/{itemTypeId}/items")
    @Secured({"ROLE_WAREMAN"})
    public List<ItemResponse> getAllItemsByType(@CurrentUser UserPrincipal userPrincipal,
                                                @PathVariable Long itemTypeId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        ItemType itemType = itemService.findItemTypeByIdAndBusinessId(itemTypeId, business.getId());
        return getAllItemResponsesByItemType(itemType);
    }

    //ruta da se doda novi item
    @PostMapping("/items")
    @Secured({"ROLE_WAREMAN"})
    public List<ItemResponse> addNewItem(@CurrentUser UserPrincipal userPrincipal,
                                                @RequestBody ItemRequest itemRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        ItemType itemType = itemService.findItemTypeByIdAndBusinessId(itemRequest.getItemTypeId(), business.getId());
        Item item = new Item(itemRequest.getName(), itemRequest.getUnit(), itemType);
        itemService.saveItem(item);
        return getAllItemResponsesByItemType(itemType);
    }

    //ruta da se obrise item
    @DeleteMapping("/items/{itemId}")
    @Secured("ROLE_WAREMAN")
    public List<ItemResponse> deleteItem(@CurrentUser UserPrincipal userPrincipal,
                                         @PathVariable Long itemId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Item item = itemService.findItemByIdAndBusiness(itemId, business.getId());
        ItemType itemType = item.getItemType();
        itemService.deleteItem(item);
        return getAllItemResponsesByItemType(itemType);
    }

    private List<ItemResponse> getAllItemResponsesByItemType(ItemType itemType){
        return itemService.getItemsByType(itemType)
                .stream()
                .map(ItemResponse::new)
                .collect(Collectors.toList());
    }

    //ruta da se proizvodu pridruzi item type
    @PutMapping("/products/itemtype")
    @Secured("ROLE_WAREMAN")
    public ApiResponse setItemTypeForProduct(@CurrentUser UserPrincipal userPrincipal,
                                             @RequestBody ProductItemTypeRequest productItemTypeRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Product product = productService.findProductById(productItemTypeRequest.getProductId(), business.getId());
        List<ProductItem> productItems = itemService.findProductItemsByProduct(product);
        if(product.getItemType() != null && !productItems.isEmpty()){
            throw new AppException("You can't change the item type before removing old items");
        }
        ItemType itemType = itemService.findItemTypeByIdAndBusinessId(
                productItemTypeRequest.getItemTypeId(),
                business.getId());

        product.setItemType(itemType);
        productService.save(product);
        return new ApiResponse("Item type successfully set for this product", 200);
    }

    //ruta da se proizvodu pridruzi item (mora se slagati type)
    @PostMapping("/products/items")
    @Secured("ROLE_WAREMAN")
    public ApiResponse addItemForProduct(@CurrentUser UserPrincipal userPrincipal,
                                         @RequestBody ProductItemRequest productItemRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Product product = productService.findProductById(productItemRequest.getProductId(),
                business.getId());
        if(product.getItemType() == null){
            throw new AppException("First set the item type");
        }
        Item item = itemService.findItemByIdAndBusinessAndItemType(productItemRequest.getItemId(),
                business.getId(),
                product.getItemType());
        ProductItem productItem = new ProductItem(product, item, productItemRequest.getValue());
        try {
            itemService.findProductItem(product.getId(), item.getId());
        }catch (ResourceNotFoundException e){
            itemService.saveProductItem(productItem);
            return new ApiResponse("Successfully added item to product", 200);
        }

        throw new AppException("Item already exists for this product");
    }

    //ruta da se makne item sa proizvoda
    @DeleteMapping("/products/{productId}/items/{itemId}")
    @Secured("ROLE_WAREMAN")
    public ApiResponse deleteItemForProduct(@CurrentUser UserPrincipal userPrincipal,
                                            @PathVariable Long productId,
                                            @PathVariable Long itemId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        ProductItem productItem = itemService.findProductItem(productId, itemId);
        if(!productItem.getProduct().getItemType().getBusinessId().equals(business.getId())){
            throw new ResourceNotFoundException("Item doesn't exist for the product");
        }

        itemService.deleteProductItem(productItem);
        return new ApiResponse("Item successfully removed from this product's item list", 200);
    }

    @GetMapping("/products/{productId}/items")
    @Secured("ROLE_WAREMAN")
    public List<ProductItemResponse> getAllItemsForProduct(@CurrentUser UserPrincipal userPrincipal,
                                                    @PathVariable Long productId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Product product = productService.findProductById(productId, business.getId());
        return itemService.findAllProductItems(product)
                .stream()
                .map(ProductItemResponse::new)
                .collect(Collectors.toList());
    }
    //rijadova ruta za proizvode da se promijeni
    //matejeva ruta za proizvode??
}
