package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.products.Discount;
import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.requests.products.DiscountRequest;
import ba.unsa.etf.si.mainserver.requests.products.InventoryRequest;
import ba.unsa.etf.si.mainserver.requests.products.ProductRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.products.ExtendedProductResponse;
import ba.unsa.etf.si.mainserver.responses.products.OfficeInventoryResponse;
import ba.unsa.etf.si.mainserver.responses.products.ProductInventoryResponse;
import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.CashRegisterService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.products.DiscountService;
import ba.unsa.etf.si.mainserver.services.products.OfficeInventoryService;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;
    private final BusinessService businessService;
    private final OfficeService officeService;
    private final OfficeInventoryService officeInventoryService;
    private final DiscountService discountService;
    private final CashRegisterService cashRegisterService;

    public ProductController(ProductService productService, BusinessService businessService, OfficeService officeService, OfficeInventoryService officeInventoryService, DiscountService discountService, CashRegisterService cashRegisterService) {
        this.productService = productService;
        this.businessService = businessService;
        this.officeService = officeService;
        this.officeInventoryService = officeInventoryService;
        this.discountService = discountService;
        this.cashRegisterService = cashRegisterService;
    }

    @GetMapping("/products")
    public List<ExtendedProductResponse> getAllProducts(){
        return productService.getAllProductResponsesWithBusiness();
    }

    @GetMapping("/products/sale")
    public List<ExtendedProductResponse> getAllProductsOnSale(){
        return productService.getAllProductOnSaleResponsesWithBusiness();
    }


    @GetMapping("/offices/{officeId}/products")
    @Secured({"ROLE_WAREMAN","ROLE_MERCHANT","ROLE_CASHIER","ROLE_OFFICEMAN","ROLE_BARTENDER"})
    public List<ProductInventoryResponse> getAllProductsForOffice(@CurrentUser UserPrincipal userPrincipal,
                                                                  @PathVariable("officeId") Long officeId){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Business> businessOptional = businessService.findById(business.getId());
        if(businessOptional.isPresent()){
            Optional<Office> officeOptional = officeService.findById(officeId);
            if(officeOptional.isPresent()){
                if(officeOptional.get().getBusiness().getId().equals(business.getId())) {
                    return officeInventoryService.findAllProductsForOffice(officeOptional.get()).stream().
                            map(officeInventory -> new ProductInventoryResponse(officeInventory.getProduct(),
                                    officeInventory)).collect(Collectors.toList());
                }
                throw new AppException("Office with id " + officeId + " doesn't exist for business with id " + business.getId());
            }
            throw new AppException("Office with id " + officeId + " doesn't exist");
        }

        throw new AppException("Business with id " + business.getId() + " doesn't exist");
    }

    @PostMapping("/products")
    @Secured({"ROLE_WAREMAN","ROLE_MERCHANT"})
    public ProductResponse addProductFroBusiness(@CurrentUser UserPrincipal userPrincipal,
                                                       @RequestBody ProductRequest productRequest){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Long businessId = business.getId();
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            Product product = new Product(productRequest.getName(),
                    productRequest.getPrice(),
                    productRequest.getUnit());

            product.setBusiness(businessOptional.get());
            businessService.save(businessOptional.get());
            return new ProductResponse(productService.save(product));
        }
        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @PostMapping("/products/{productId}/image")
    @Secured({"ROLE_WAREMAN","ROLE_MERCHANT"})
    public ResponseEntity<ApiResponse> uploadImage(@PathVariable Long productId, @RequestParam("image") MultipartFile multipartFile,
                                                   @CurrentUser UserPrincipal userPrincipal) {
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        try {
            Optional<Product> optionalProduct = productService.findById(productId);
            if (!optionalProduct.isPresent()) {
                throw new ResourceNotFoundException("Product does not exist");
            }
            Product product = optionalProduct.get();
            if (!product.getBusiness().getId().equals(business.getId())) {
                throw new UnauthorizedException("Not your product");
            }
            product.setImage(multipartFile.getBytes());
            productService.save(product);
            return ResponseEntity.ok(new ApiResponse("Image uploaded successfully", 200));
        } catch (IOException e) {
            throw new BadParameterValueException("Image not sent as image or format not ok");
        }
    }

    @PutMapping("/products/{productId}")
    @Secured({"ROLE_WAREMAN", "ROLE_MERCHANT"})
    public ProductResponse updateProductForBusiness(@PathVariable("productId") Long productId,
                                                 @RequestBody ProductRequest productRequest,
                                                    @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Product> optionalProduct = productService.findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        Product product = optionalProduct.get();
        if (!product.getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your product");
        }

        product.setName(productRequest.getName());
        product.setPrice(product.getPrice());
        product.setUnit(productRequest.getUnit());

        return new ProductResponse(productService.save(product));
    }

    //TODO popraviti ovo
    @DeleteMapping("/products/{productId}")
    @Secured("ROLE_WAREMAN")
    public ResponseEntity<?> deleteProductForBusiness(@PathVariable("productId") Long productId,
                                                      @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Product> optionalProduct = productService.findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        Product product = optionalProduct.get();
        if (!product.getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your product");
        }

        productService.delete(product);
        return ResponseEntity.ok(new ApiResponse("Product successfully deleted", 200));
    }


    @PostMapping("/inventory")
    @Secured("ROLE_WAREMAN")
    public OfficeInventoryResponse addInventoryForBusiness(@RequestBody InventoryRequest inventoryRequest,
                                                           @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Product> optionalProduct = productService.findById(inventoryRequest.getProductId());
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        Product product = optionalProduct.get();
        if (!product.getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your product");
        }

        Optional<Office> optionalOffice = officeService.findById(inventoryRequest.getOfficeId());
        if (!optionalOffice.isPresent()) {
            throw new ResourceNotFoundException("Office does not exist");
        }
        Office office = optionalOffice.get();
        if (!office.getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your office");
        }

        OfficeInventory officeInventory = new OfficeInventory(office, product, inventoryRequest.getQuantity());
        return new OfficeInventoryResponse(
                officeInventoryService.save(officeInventory),
                cashRegisterService.getAllCashRegisterResponsesByOfficeId(
                        office.getId()
                )
        );
    }

    @PutMapping("/inventory")
    @Secured("ROLE_WAREMAN")
    public OfficeInventoryResponse updateInventoryForBusiness(@RequestBody InventoryRequest inventoryRequest,
                                                              @CurrentUser UserPrincipal userPrincipal) {
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Product> optionalProduct = productService.findById(inventoryRequest.getProductId());
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        Product product = optionalProduct.get();
        if (!product.getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your product");
        }

        Optional<Office> optionalOffice = officeService.findById(inventoryRequest.getOfficeId());
        if (!optionalOffice.isPresent()) {
            throw new ResourceNotFoundException("Office does not exist");
        }
        Office office = optionalOffice.get();
        if (!office.getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your office");
        }

        Optional<OfficeInventory> officeInventoryOptional = officeInventoryService.
                findByProductAndOffice(product, office);
        if (officeInventoryOptional.isPresent()) {
            officeInventoryOptional.get().setOffice(office);
            officeInventoryOptional.get().setProduct(product);
            officeInventoryOptional.get().setQuantity(inventoryRequest.getQuantity());
            return new OfficeInventoryResponse(
                    officeInventoryService.save(officeInventoryOptional.get()),
                    cashRegisterService.getAllCashRegisterResponsesByOfficeId(
                            office.getId()
                    )
            );
        }

        throw new AppException("Use POST request");

    }


    @PutMapping("/products/{productId}/discount")
    @Secured("ROLE_MERCHANT")
    public ProductResponse updateDiscount(@PathVariable("productId") Long productId,
                                        @RequestBody DiscountRequest discountRequest,
                                          @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Product> optionalProduct = productService.findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        Product product = optionalProduct.get();
        if (!product.getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your product");
        }

        Discount discount = new Discount(discountRequest.getPercentage());
        product.setDiscount(discount);
        return new ProductResponse(productService.save(product));
    }

}
