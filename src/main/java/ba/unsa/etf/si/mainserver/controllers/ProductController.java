package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.requests.products.InventoryRequest;
import ba.unsa.etf.si.mainserver.requests.products.ProductRequest;
import ba.unsa.etf.si.mainserver.responses.products.InventoryResponse;
import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business/{businessId}")
public class ProductController {
    private final ProductService productService;
    //private final BusinessService businessService;
    //tamo implementirati
    //    public Optional<Business> getBusinessByProductId(Long productId){
    //
    //    }

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    @Secured("ROLE_ADMIN")
    public List<ProductResponse> getAllProductsForBusiness(@PathVariable("businessId") Long businessId){
        //TODO implement this when you add products to the office
        //return productService.findAllByBusinessId(businessId);
        return null;
    }

    @GetMapping("/offices/{officeId}/products")
    @Secured("ROLE_ADMIN")
    public List<InventoryResponse> getAllProductsForOffice(@PathVariable("businessId") Long businessId,
                                                           @PathVariable("officeId") Long officeId){
        //TODO implement this when you add products to the office
        return null;
    }

    @PostMapping("/products")
    @Secured("ROLE_ADMIN")
    public ProductResponse addProductFroBusiness(@PathVariable("businessId") Long businessId,
                                                       @RequestBody ProductRequest productRequest){
        //TODO implement this when you add products to the office
        return null;
    }

    @PutMapping("/products/{productId}")
    @Secured("ROLE_ADMIN")
    public ProductResponse updateProductForBusiness(@PathVariable("businessId") Long businessId,
                                         @PathVariable("productId") Long productId,
                                                 @RequestBody ProductRequest productRequest){
        //TODO implement this when you add products to the office
        return null;
    }

    @DeleteMapping("/products/{productId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteProductForBusiness(@PathVariable("businessId") Long businessId,
                                                   @PathVariable("productId") Long productId){
        //TODO implement this when you add products to the office
        return null;
    }

    @PostMapping("/inventory")
    @Secured("ROLE_ADMIN")
    public ProductResponse addInventoryForBusiness(@PathVariable("businessId") Long businessId,
                                        @RequestBody InventoryRequest inventoryRequest){
        //TODO implement this when you add products to the office
        return null;
    }
}
