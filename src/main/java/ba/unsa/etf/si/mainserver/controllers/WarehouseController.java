package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.Warehouse;
import ba.unsa.etf.si.mainserver.repositories.products.WarehouseRepository;
import ba.unsa.etf.si.mainserver.requests.products.WarehouseRequest;
import ba.unsa.etf.si.mainserver.responses.products.WarehouseResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
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

    public WarehouseController(WarehouseRepository warehouseRepository,
                               BusinessService businessService,
                               ProductService productService,
                               WarehouseLogService warehouseLogService) {
        this.warehouseRepository = warehouseRepository;
        this.businessService = businessService;
        this.productService = productService;
        this.warehouseLogService = warehouseLogService;
    }

    @PostMapping
    @Secured("ROLE_WAREMAN")
    public WarehouseResponse registerIncomingProducts(@CurrentUser UserPrincipal userPrincipal,
                                                   @RequestBody WarehouseRequest warehouseRequest){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Product> optionalProduct = productService.findById(warehouseRequest.getProductId());
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        Product product = optionalProduct.get();
        if (!product.getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your product");
        }

        Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProduct(product);
        Warehouse warehouse;
        if(!optionalWarehouse.isPresent()){
            warehouse = new Warehouse(business, product, warehouseRequest.getQuantity());
        }else{
            warehouse = optionalWarehouse.get();
            warehouse.setQuantity(warehouseRequest.getQuantity() + warehouse.getQuantity());
        }

        warehouseLogService.logNewDelivery(warehouse, warehouseRequest.getQuantity());

        return new WarehouseResponse(warehouseRepository.save(warehouse));
    }

//    @PutMapping
//    @Secured("ROLE_WAREMAN")
//    public WarehouseResponse addMoreOfProduct(@CurrentUser UserPrincipal userPrincipal,
//                                                      @RequestBody WarehouseRequest warehouseRequest){
//        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
//        Optional<Product> optionalProduct = productService.findById(warehouseRequest.getProductId());
//        if (!optionalProduct.isPresent()) {
//            throw new ResourceNotFoundException("Product does not exist");
//        }
//        Product product = optionalProduct.get();
//        if (!product.getBusiness().getId().equals(business.getId())) {
//            throw new UnauthorizedException("Not your product");
//        }
//
//        Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProduct(product);
//        Warehouse warehouse;
//        if(!optionalWarehouse.isPresent()){
//            warehouse = new Warehouse(business, product, warehouseRequest.getQuantity());
//        }else{
//            warehouse = optionalWarehouse.get();
//            warehouse.setQuantity(warehouse.getQuantity() + warehouseRequest.getQuantity());
//        }
//
//        warehouseLogService.logNewDelivery(warehouse);
//
//        return new WarehouseResponse(warehouseRepository.save(warehouse));
//    }

    @GetMapping
    @Secured("ROLE_WAREMAN")
    public List<WarehouseResponse> getEverythingInAWarehouse(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        return warehouseRepository.findAllByBusiness(business)
                .stream()
                .map(WarehouseResponse::new)
                .collect(Collectors.toList());
    }


}
