package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.products.Discount;
import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.requests.products.DiscountRequest;
import ba.unsa.etf.si.mainserver.requests.products.InventoryRequest;
import ba.unsa.etf.si.mainserver.requests.products.ProductRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.products.DiscountResponse;
import ba.unsa.etf.si.mainserver.responses.products.OfficeInventoryResponse;
import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.products.DiscountService;
import ba.unsa.etf.si.mainserver.services.products.OfficeInventoryService;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/business/{businessId}")
public class ProductController {
    private final ProductService productService;
    private final BusinessService businessService;
    private final OfficeService officeService;
    private final OfficeInventoryService officeInventoryService;
    private final DiscountService discountService;

    public ProductController(ProductService productService, BusinessService businessService, OfficeService officeService, OfficeInventoryService officeInventoryService, DiscountService discountService) {
        this.productService = productService;
        this.businessService = businessService;
        this.officeService = officeService;
        this.officeInventoryService = officeInventoryService;
        this.discountService = discountService;
    }

    @GetMapping("/products")
    @Secured("ROLE_ADMIN")
    public List<ProductResponse> getAllProductsForBusiness(@PathVariable("businessId") Long businessId){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            return businessOptional.get().getProducts().stream().map(ProductResponse::new).collect(Collectors.toList());
        }

        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @GetMapping("/offices/{officeId}/products")
    @Secured("ROLE_ADMIN")
    public List<OfficeInventoryResponse> getAllProductsForOffice(@PathVariable("businessId") Long businessId,
                                                                 @PathVariable("officeId") Long officeId){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            Optional<Office> officeOptional = officeService.findById(officeId);
            if(officeOptional.isPresent()){
                return officeInventoryService.findAllProductsForOffice(officeOptional.get()).stream().
                        map(OfficeInventoryResponse::new).collect(Collectors.toList());
            }
            throw new AppException("Office with id " + officeId + " doesn't exist");
        }

        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @PostMapping("/products")
    @Secured("ROLE_ADMIN")
    public ProductResponse addProductFroBusiness(@PathVariable("businessId") Long businessId,
                                                       @RequestBody ProductRequest productRequest){

        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            try {
                Product product = new Product(productRequest.getName(),
                        productRequest.getPrice(),
                        productRequest.getUnit(),
                        productRequest.getImage());

                Discount discount = new Discount(0);
                product.setBusiness(businessOptional.get());
                product.setDiscount(discount);
                businessService.save(businessOptional.get());
                return new ProductResponse(productService.save(product));
            } catch (IOException e) {
                throw new AppException("Invalid image");
            }
        }
        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @PutMapping("/products/{productId}")
    @Secured("ROLE_ADMIN")
    public ProductResponse updateProductForBusiness(@PathVariable("businessId") Long businessId,
                                         @PathVariable("productId") Long productId,
                                                 @RequestBody ProductRequest productRequest){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
                Optional<Product> productOptional = productService.findById(productId);
                if(productOptional.isPresent()){
                    productOptional.get().setName(productRequest.getName());
                    productOptional.get().setPrice(productRequest.getPrice());
                    productOptional.get().setImage(productRequest.getImage());
                    productOptional.get().setUnit(productRequest.getUnit());
                    return new ProductResponse(productService.save(productOptional.get()));
                }
                throw new AppException("Product with id " + productId + " doesn't exist");
        }
        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @DeleteMapping("/products/{productId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteProductForBusiness(@PathVariable("businessId") Long businessId,
                                                   @PathVariable("productId") Long productId){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            Optional<Product> productOptional = productService.findById(productId);
            if(productOptional.isPresent()){
                if(productOptional.get().getBusiness().getId().equals(businessId)){
                    officeInventoryService.findByProduct(productOptional.get()).forEach(officeInventoryService::delete);
                    productService.delete(productOptional.get());
                    return ResponseEntity.ok(new ApiResponse("Product successfully deleted", 200));
                }
                throw new AppException("Product with id " + productId + " doesn't exist for business with id" + businessId);
            }
            throw new AppException("Product with id " + productId + " doesn't exist");
        }
        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @PostMapping("/inventory")
    @Secured("ROLE_ADMIN")
    public OfficeInventoryResponse addInventoryForBusiness(@PathVariable("businessId") Long businessId,
                                        @RequestBody InventoryRequest inventoryRequest){

        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            Optional<Product> productOptional = productService.findById(inventoryRequest.getProductId());
            if(productOptional.isPresent()){
                if(productOptional.get().getBusiness().getId().equals(businessId)){
                    Optional<Office> officeOptional = officeService.findByBusiness(businessOptional.get());
                if(officeOptional.isPresent()){
                    OfficeInventory officeInventory = new OfficeInventory(officeOptional.get(),
                            productOptional.get(),
                            inventoryRequest.getQuantity());
                    return new OfficeInventoryResponse(officeInventoryService.save(officeInventory));
                }
                throw new AppException("Office with id " + inventoryRequest.getOfficeId() + " doesn't exist for business with id" + businessId);
                }
                throw new AppException("Product with id " + inventoryRequest.getProductId() + " doesn't exist for business with id" + businessId);
            }
            throw new AppException("Product with id " + inventoryRequest.getProductId() + " doesn't exist");
        }

        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @PutMapping("/inventory")
    @Secured("ROLE_ADMIN")
    public OfficeInventoryResponse updateInventoryForBusiness(@PathVariable("businessId") Long businessId,
                                                               @RequestBody InventoryRequest inventoryRequest) {
        Optional<Business> businessOptional = businessService.findById(businessId);
        if (businessOptional.isPresent()) {
            Optional<Product> productOptional = productService.findById(inventoryRequest.getProductId());
            if (productOptional.isPresent()) {
                if (productOptional.get().getBusiness().getId().equals(businessId)) {
                    Optional<Office> officeOptional = officeService.findByBusiness(businessOptional.get());
                    if (officeOptional.isPresent()) {
                        Optional<OfficeInventory> officeInventoryOptional = officeInventoryService.
                                findByProductAndOffice(productOptional.get(), officeOptional.get());
                        if (officeInventoryOptional.isPresent()) {
                            officeInventoryOptional.get().setOffice(officeOptional.get());
                            officeInventoryOptional.get().setProduct(productOptional.get());
                            officeInventoryOptional.get().setQuantity(inventoryRequest.getQuantity());
                            return new OfficeInventoryResponse(officeInventoryService.save(officeInventoryOptional.get()));
                        }
                        throw new AppException("Office with id " + inventoryRequest.getOfficeId() + " doesn't exist for business with id" + businessId);
                    }
                    throw new AppException("Product with id " + inventoryRequest.getProductId() + " doesn't exist for business with id" + businessId);
                }
                throw new AppException("Product with id " + inventoryRequest.getProductId() + " doesn't exist");
            }
        }
        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

//ova ruta ne valja zato sto uvijek dodam discount kad dodajem proizvod pa ga treba samo promijeniti
    //ne valja i kad uradim get nakon ovoga
    @PutMapping("/products/{productId}/discount")
    @Secured("ROLE_ADMIN")
    public DiscountResponse addDiscount(@PathVariable("businessId") Long businessId,
                                        @PathVariable("productId") Long productId,
                                        @RequestBody DiscountRequest discountRequest){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            Optional<Product> productOptional = productService.findById(productId);
            if (productOptional.isPresent()){
                if(productOptional.get().getBusiness().getId().equals(businessId)){
                    Optional<Discount> discountOptional = discountService.findById(productOptional.get().getDiscount().getId());
                    if(discountOptional.isPresent()){
                        System.out.println(discountOptional.get().getId());
                        discountOptional.get().setPercentage(discountRequest.getPercentage());
                        return new DiscountResponse(discountService.save(discountOptional.get()));
                    }
                }
                throw new AppException("Product with id " + productId+ " doesn't exist for business with id" + businessId);
            }
            throw new AppException("Product with id " + productId+ " doesn't exist");
        }

        throw new AppException("Business with id " + businessId + " doesn't exist");
    }
}
