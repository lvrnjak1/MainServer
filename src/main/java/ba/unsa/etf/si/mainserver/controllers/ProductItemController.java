package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.items.ItemType;
import ba.unsa.etf.si.mainserver.requests.products.items.ItemRequest;
import ba.unsa.etf.si.mainserver.requests.products.items.ItemTypeRequest;
import ba.unsa.etf.si.mainserver.requests.products.items.ProductItemRequest;
import ba.unsa.etf.si.mainserver.requests.products.items.ProductItemTypeRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.products.items.ItemResponse;
import ba.unsa.etf.si.mainserver.responses.products.items.ItemTypeResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.products.ItemService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductItemController {
    private final BusinessService businessService;
    private final ItemService itemService;

    public ProductItemController(BusinessService businessService,
                                 ItemService itemService) {
        this.businessService = businessService;
        this.itemService = itemService;
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
        itemService.save(itemType);
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
        ItemType itemType = itemService.findByIdAndBusinessId(typeId, business.getId());
        itemService.delete(itemType);
        return getAllItemTypeResponsesByBusiness(business);
    }

    //ruta da se dobiju svi itemovi po type-u
    @GetMapping("/itemtypes/{itemTypeId}/items")
    @Secured({"ROLE_WAREMAN"})
    public List<ItemResponse> getAllItemsByType(@CurrentUser UserPrincipal userPrincipal,
                                                @PathVariable Long itemTypeId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        ItemType itemType = itemService.findByIdAndBusinessId(itemTypeId, business.getId());
        return itemService.getItemsByType(itemType)
                .stream()
                .map(ItemResponse::new)
                .collect(Collectors.toList());
    }

    //ruta da se doda novi item
    @PostMapping("/itemtypes/{itemTypeId}/items")
    @Secured({"ROLE_WAREMAN"})
    public List<ItemResponse> addNewItem(@CurrentUser UserPrincipal userPrincipal,
                                                @PathVariable Long itemTypeId,
                                                @RequestBody ItemRequest itemRequest){
        return null;
    }

    //ruta da se obrise item

    //ruta da se proizvodu pridruzi item type
    @PutMapping("/products/itemtype")
    @Secured("ROLE_WAREMAN")
    public ApiResponse setItemTypeForProduct(@CurrentUser UserPrincipal userPrincipal,
                                             @RequestBody ProductItemTypeRequest productItemTypeRequest){
        //treba obrisat staro ili ne dozvolit promjenu, bolje obrisati stare iteme??
        return null;
    }

    //ruta da se proizvodu pridruzi item (mora se slagati type)
    @GetMapping("/products/items")
    @Secured("ROLE_WAREMAN")
    public ApiResponse addItemForProduct(@CurrentUser UserPrincipal userPrincipal,
                                         @RequestBody ProductItemRequest productItemRequest){
        return null;
    }
}
