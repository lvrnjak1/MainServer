package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.configurations.Actions;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.Warehouse;
import ba.unsa.etf.si.mainserver.repositories.products.WarehouseRepository;
import ba.unsa.etf.si.mainserver.requests.products.WarehouseRequest;
import ba.unsa.etf.si.mainserver.responses.products.QuantityResponse;
import ba.unsa.etf.si.mainserver.responses.products.ShippingResponse;
import ba.unsa.etf.si.mainserver.responses.products.WarehouseLogResponse;
import ba.unsa.etf.si.mainserver.responses.products.WarehouseResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.products.InventoryLogService;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import ba.unsa.etf.si.mainserver.services.products.WarehouseLogService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {
    private final WarehouseRepository warehouseRepository;
    private final BusinessService businessService;
    private final ProductService productService;
    private final WarehouseLogService warehouseLogService;
    private final LogServerService logServerService;
    private final InventoryLogService inventoryLogService;

    public WarehouseController(WarehouseRepository warehouseRepository,
                               BusinessService businessService,
                               ProductService productService,
                               WarehouseLogService warehouseLogService,
                               LogServerService logServerService,
                               InventoryLogService inventoryLogService) {
        this.warehouseRepository = warehouseRepository;
        this.businessService = businessService;
        this.productService = productService;
        this.warehouseLogService = warehouseLogService;
        this.logServerService = logServerService;
        this.inventoryLogService = inventoryLogService;
    }

    @PostMapping
    @Secured("ROLE_WAREMAN")
    public WarehouseResponse registerIncomingProducts(@CurrentUser UserPrincipal userPrincipal,
                                                   @RequestBody WarehouseRequest warehouseRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Product product = productService.findProductById(warehouseRequest.getProductId(), business.getId());

        Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProduct(product);
        Warehouse warehouse;
        if(!optionalWarehouse.isPresent()){
            warehouse = new Warehouse(business, product, warehouseRequest.getQuantity());
        }else{
            warehouse = optionalWarehouse.get();
            warehouse.setQuantity(warehouseRequest.getQuantity() + warehouse.getQuantity());
        }

        warehouseLogService.logNewDelivery(warehouse, warehouseRequest.getQuantity());
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.REGISTER_INCOMING_PRODUCTS_ACTION_NAME,
                "warehouse",
                "Employee " + userPrincipal.getUsername() + " has registered product("
                        + product.getName() + ", quantity = " + warehouseRequest.getQuantity() + ")!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER

        return new WarehouseResponse(warehouseRepository.save(warehouse));
    }


    @GetMapping
    @Secured("ROLE_WAREMAN")
    public List<WarehouseResponse> getEverythingInAWarehouse(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return warehouseRepository.findAllByBusiness(business)
                .stream()
                .map(WarehouseResponse::new)
                .collect(Collectors.toList());
    }


    @GetMapping("/log")
    @Secured("ROLE_WAREMAN")
    public List<WarehouseLogResponse> getWarehouseLogs(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return  warehouseLogService.findAllByBusiness(business)
                .stream()
                .map(WarehouseLogResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/products/{productId}")
    @Secured("ROLE_WAREMAN")
    public QuantityResponse getQuantityByProductId(@PathVariable Long productId,
                                                   @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Optional<Product> productOptional = productService.findById(productId);
        if(!productOptional.isPresent()){
            throw new ResourceNotFoundException("Products doesn't exist");
        }

        if(!productOptional.get().getBusiness().getId().equals(business.getId())){
            throw new UnauthorizedException("Not your product");
        }

        Optional<Warehouse> warehouseOptional = warehouseRepository.findByProduct(productOptional.get());
        if(!warehouseOptional.isPresent()){
            throw new ResourceNotFoundException("Your warehouse doesn't have this product");
        }

        return new QuantityResponse(productId, warehouseOptional.get().getQuantity());
    }

    @GetMapping("/shipping")
    @Secured("ROLE_WAREMAN")
    public List<ShippingResponse> getShippingInfo(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return inventoryLogService.getShippingLogForBusiness(business);
    }
}
