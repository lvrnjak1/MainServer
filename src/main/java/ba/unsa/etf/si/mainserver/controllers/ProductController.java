package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.configurations.Actions;
import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.products.*;
import ba.unsa.etf.si.mainserver.repositories.PDVRepository;
import ba.unsa.etf.si.mainserver.repositories.products.WarehouseRepository;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationPayload;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationRequest;
import ba.unsa.etf.si.mainserver.requests.products.*;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.products.*;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.CashRegisterService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.products.CommentService;
import ba.unsa.etf.si.mainserver.services.products.ItemService;
import ba.unsa.etf.si.mainserver.services.products.OfficeInventoryService;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    private final CashRegisterService cashRegisterService;
    private final WarehouseRepository warehouseRepository;
    private final CommentService commentService;
    private final LogServerService logServerService;
    private final PDVRepository pdvRepository;
    private final ItemService itemService;

    public ProductController(ProductService productService,
                             BusinessService businessService,
                             OfficeService officeService,
                             OfficeInventoryService officeInventoryService,
                             CashRegisterService cashRegisterService,
                             WarehouseRepository warehouseRepository,
                             CommentService commentService,
                             LogServerService logServerService, PDVRepository pdvRepository, ItemService itemService) {
        this.productService = productService;
        this.businessService = businessService;
        this.officeService = officeService;
        this.officeInventoryService = officeInventoryService;
        this.warehouseRepository = warehouseRepository;
        this.cashRegisterService = cashRegisterService;
        this.commentService = commentService;
        this.logServerService = logServerService;
        this.pdvRepository = pdvRepository;
        this.itemService = itemService;
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
    @Secured({"ROLE_WAREMAN","ROLE_MERCHANT","ROLE_CASHIER","ROLE_OFFICEMAN","ROLE_BARTENDER", "ROLE_SERVER"})
    public List<ProductInventoryResponse> getAllProductsForOffice(@CurrentUser UserPrincipal userPrincipal,
                                                                  @PathVariable("officeId") Long officeId){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Office office = officeService.findOfficeById(officeId, business.getId());

        return officeInventoryService.findAllProductsForOffice(office).stream().
                map(officeInventory -> new ProductInventoryResponse(officeInventory.getProduct(),
                        officeInventory,
                        itemService.findAllProductItems(officeInventory.getProduct())))
                .collect(Collectors.toList());
    }

    @PostMapping("/products")
    @Secured({"ROLE_WAREMAN","ROLE_MERCHANT"})
    public ProductResponse addProductForBusiness(@CurrentUser UserPrincipal userPrincipal,
                                                 @RequestBody ProductRequest productRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        pdvRepository.findByPdvRate(productRequest.getPdv())
                .orElseThrow(() -> new BadParameterValueException("PDV rate doesn't exist"));
        Product product = new Product(productRequest.getName(),
                productRequest.getPrice(),
                productRequest.getUnit(),
                productRequest.getBarcode(),
                productRequest.getDescription(),
                productRequest.getPdv());

        product.setBusiness(business);
        businessService.save(business);
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.CREATE_PRODUCT_ACTION_NAME,
                "product",
                "Employee " + userPrincipal.getUsername() + " has created product: " + product.getName()
                        + "!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        Product savedProduct = productService.save(product);
        return new ProductResponse(savedProduct);
    }

    @PostMapping("/products/{productId}/image")
    @Secured({"ROLE_WAREMAN","ROLE_MERCHANT"})
    public ResponseEntity<ApiResponse> uploadImage(@PathVariable Long productId,
                                                   @RequestParam("image") MultipartFile multipartFile,
                                                   @CurrentUser UserPrincipal userPrincipal) {
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        try {
            Optional<Product> optionalProduct = productService.findById(productId);
            Product product = productService.findProductById(productId, business.getId());
            product.setImage(multipartFile.getBytes());
            productService.save(product);
            // DO NOT EDIT THIS CODE BELOW, EVER
            logServerService.documentAction(
                    userPrincipal.getUsername(),
                    Actions.UPLOAD_IMAGE_ACTION_NAME,
                    "image",
                    "Employee " + userPrincipal.getUsername() + " has uploaded image for product " +
                            product.getName() + "!"
            );
            // DO NOT EDIT THIS CODE ABOVE, EVER
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
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Product product = productService.findProductById(productId, business.getId());
        pdvRepository.findByPdvRate(productRequest.getPdv())
                .orElseThrow(() -> new BadParameterValueException("PDV rate doesn't exist"));
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setUnit(productRequest.getUnit());
        product.setBarcode(productRequest.getBarcode());
        product.setDescription(productRequest.getDescription());
        product.setPdv(productRequest.getPdv());
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.UPDATE_PRODUCT_ACTION_NAME,
                "product",
                "Employee " + userPrincipal.getUsername() + " has updated the product " +
                        product.getName() + "!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return new ProductResponse(productService.save(product));
    }

    @DeleteMapping("/products/{productId}")
    @Secured("ROLE_WAREMAN")
    public ResponseEntity<?> deleteProductForBusiness(@PathVariable("productId") Long productId,
                                                      @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Product product = productService.findProductById(productId, business.getId());
        productService.delete(product);
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.DELETE_PRODUCT_ACTION_NAME,
                "product",
                "EMPLOYEE " + userPrincipal.getUsername() + " has deleted the product " +
                        product.getName() + "!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return ResponseEntity.ok(new ApiResponse("Product successfully deleted", 200));
    }


    @PostMapping("/inventory")
    @Secured("ROLE_WAREMAN")
    public OfficeInventoryResponse addInventoryForOffice(@RequestBody InventoryRequest inventoryRequest,
                                                         @CurrentUser UserPrincipal userPrincipal){
       return addOneInventory(userPrincipal, inventoryRequest, true);
    }

    private OfficeInventoryResponse addOneInventory(UserPrincipal userPrincipal, InventoryRequest inventoryRequest,
                                                    boolean notifyCashRegister){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Product product = productService.findProductById(inventoryRequest.getProductId(), business.getId());

        Office office = officeService.findOfficeById(inventoryRequest.getOfficeId(), business.getId());

        Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductAndBusiness(product, business);
        if(!optionalWarehouse.isPresent()){
            throw new ResourceNotFoundException("This product is not in your warehouse");
        }

        if(optionalWarehouse.get().getQuantity() < inventoryRequest.getQuantity()){
            throw new AppException("You don't have enough of this product to transfer this quantity");
        }

        double quantity = optionalWarehouse.get().getQuantity();
        optionalWarehouse.get().setQuantity(quantity - inventoryRequest.getQuantity());

        Optional<OfficeInventory> officeInventoryOptional = officeInventoryService.
                findByProductAndOffice(product, office);
        OfficeInventory officeInventory = null;
        if (officeInventoryOptional.isPresent()) {
            officeInventory = officeInventoryOptional.get();
            officeInventoryOptional.get().setOffice(office);
            officeInventoryOptional.get().setProduct(product);
            double officeQuantity = officeInventoryOptional.get().getQuantity();
            officeInventoryOptional.get().setQuantity(inventoryRequest.getQuantity() + officeQuantity);
            officeInventoryService.logDelivery(officeInventoryOptional.get(), inventoryRequest.getQuantity());
//            return new OfficeInventoryResponse(
//                    officeInventoryService.save(officeInventoryOptional.get()));
        }
        else{
            officeInventory = new OfficeInventory(office, product, inventoryRequest.getQuantity());
            officeInventoryService.logDelivery(officeInventory, inventoryRequest.getQuantity());
        }

        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.PRODUCTS_TO_OFFICE_ACTION_NAME,
                "office",
                "Employee " + userPrincipal.getUsername() + " has added product " +
                        product.getName() + " to office " + office.getContactInformation().getCity() + "!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        if(notifyCashRegister) {
            logServerService.broadcastNotification(
                    new NotificationRequest(
                            "info",
                            new NotificationPayload(
                                    product.getName(),
                                    "office_products_add",
                                    inventoryRequest.getQuantity() + " " +
                                            product.getName() + " have been added to the office in " +
                                            office.getContactInformation().getCity() + " " +
                                            office.getContactInformation().getAddress() + "(" + office.getId() + ")"
                            )
                    ),
                    "merchant_dashboard"
            );
            String notifDescription = String.format("{\"businessId\":%d, \"officeId\":%d, " +
                            "\"inventory\": [{\"productName\":\"%s\", \"productQuantity\":%.2f}]}", business.getId(),
                    office.getId(), product.getName(), inventoryRequest.getQuantity());

            logServerService.broadcastNotification(
                    new NotificationRequest(
                            "info",
                            new NotificationPayload(
                                    "products added",
                                    "office_products_add",
                                    notifDescription
                            )
                    ),
                    "cash_server"
            );
        }
        return new OfficeInventoryResponse(
                officeInventoryService.save(officeInventory));
    }

    @PostMapping("/inventory/batch")
    @Secured("ROLE_WAREMAN")
    public List<OfficeInventoryResponse> addInventoriesForOffice(@CurrentUser UserPrincipal userPrincipal,
                                                                 @RequestBody BatchInventoryRequest batchInventoryRequest){
        if(batchInventoryRequest.getInventory().isEmpty()){
            return new ArrayList<>();
        }

        List<OfficeInventoryResponse> inventory = batchInventoryRequest.getInventory().stream()
                .map(inventoryRequest -> addOneInventory(userPrincipal, inventoryRequest,false))
                .collect(Collectors.toList());
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Office office = officeService.findOfficeById(inventory.get(0).getOfficeId(), business.getId());

        String array = "[";
        array += batchInventoryRequest.getInventory().stream().map(officeInventory -> {
            Product product = productService.findProductById(officeInventory.getProductId(), business.getId());
            return String.format("{\"productName\":\"%s\", \"productQuantity\":%.2f}",
                    product.getName(), officeInventory.getQuantity());
        }).collect(Collectors.joining(","));
        array += "]";
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "products added",
                                "office_products_add",
                                String.format("{\"businessId\":%d, \"officeId\":%d, " +
                                                "\"inventory\":" + array + "}",
                                        business.getId(),
                                        office.getId())
                        )
                ),
                "cash_server"
        );
        String description = "Following products have been transferred to office at "
                + office.getContactInformation().getAddress() + ":\n" +
                batchInventoryRequest.getInventory().stream()
                .map(inventoryRequest -> {
                    Product product = productService.findProductById(inventoryRequest.getProductId(), business.getId());
                    return inventoryRequest.getQuantity() + " " + product.getUnit() + " of " + product.getName();
                })
                .collect(Collectors.joining(", "));
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "request accepted",
                                "office_products_add",
                                description
                        )
                ),
                "merchant_dashboard"
        );
        return inventory;
    }

    @GetMapping("/inventory/log")
    @Secured("ROLE_WAREMAN")
    public List<InventoryLogResponse> getInventoryLogs(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return officeInventoryService.findAllByBusiness(business)
                .stream()
                .map(InventoryLogResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/products/{productId}/comment")
    public CommentResponse addCommentForProduct(@PathVariable("productId") Long productId,
                                                @RequestBody CommentRequest commentRequest) {
        Optional<Product> optionalProduct = productService.findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        Product product = optionalProduct.get();
        Comment comment = new Comment(product, commentRequest.getFirstName(), commentRequest.getLastName(), commentRequest.getEmail(), commentRequest.getText());
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "comment",
                                "comment_add",
                                "A new comment on " + product.getName() + " (" + product.getBarcode()+") - " + product.getBusiness().getName()
                        )
                ),
                "public_relations"
        );
        return new CommentResponse(commentService.save(comment));
    }

    @GetMapping("/products/{productId}/comments")
    public List<CommentResponse> getCommentsForProduct(@PathVariable("productId") Long productId) {

        return commentService.findByProductId(productId)
                .stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/products/comments")
    public List<CommentResponse> getAllComments() {

        return commentService.findAll()
                .stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/products/{productId}/comments/{commentId}")
    @Secured({"ROLE_PRW", "ROLE_PRP"})
    public ResponseEntity<?> deleteCommentForProduct(
            @PathVariable("productId") Long productId,
            @PathVariable("commentId") Long commentId,
            @CurrentUser UserPrincipal userPrincipal) {
        Optional<Product> optionalProduct = productService.findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (!optionalComment.isPresent()) {
            throw new ResourceNotFoundException("Comment does not exist");
        }
        Comment comment = optionalComment.get();
        commentService.delete(comment);
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.DELETE_COMMENT_ON_PRODUCT_ACTION_NAME,
                "comment",
                "Employee " + userPrincipal.getUsername() + " has deleted the comment " +
                        comment.getText() + "!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return ResponseEntity.ok(new ApiResponse("Comment successfully deleted", 200));
    }

    @PutMapping("/products/{productId}/discount")
    @Secured({"ROLE_MERCHANT", "ROLE_WAREMAN"})
    public ProductResponse updateDiscount(@PathVariable("productId") Long productId,
                                          @RequestBody DiscountRequest discountRequest,
                                          @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Product product = productService.findProductById(productId, business.getId());

        if(product.getDiscount() != null){
            product.getDiscount().setPercentage(discountRequest.getPercentage());
        }else{
            Discount discount = new Discount(discountRequest.getPercentage());
            product.setDiscount(discount);
        }

        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.UPDATE_DISCOUNT_ACTION_NAME,
                "product",
                "Employee " + userPrincipal.getUsername()
                        + " has updated discount for product " +
                        product.getName() + "!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return new ProductResponse(productService.save(product));
    }
}
