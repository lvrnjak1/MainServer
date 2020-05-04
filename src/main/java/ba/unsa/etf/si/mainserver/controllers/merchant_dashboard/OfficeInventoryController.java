package ba.unsa.etf.si.mainserver.controllers.merchant_dashboard;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.merchant_warehouse.OfficeProductRequest;
import ba.unsa.etf.si.mainserver.models.merchant_warehouse.ProductQuantity;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.repositories.auth.UserRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.merchant_warehouse.OfficeProductRequestRepository;
import ba.unsa.etf.si.mainserver.repositories.merchant_warehouse.ProductQuantityRepository;
import ba.unsa.etf.si.mainserver.repositories.products.ProductRepository;
import ba.unsa.etf.si.mainserver.repositories.products.WarehouseRepository;
import ba.unsa.etf.si.mainserver.requests.merchant_dashboard.OfficeInventoryRequest;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationPayload;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationRequest;
import ba.unsa.etf.si.mainserver.requests.warehouse.RequestAnswer;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.products.DiscountResponse;
import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import ba.unsa.etf.si.mainserver.responses.products.WarehouseResponse;
import ba.unsa.etf.si.mainserver.responses.warehouse.OfficeInventoryRequestResponse;
import ba.unsa.etf.si.mainserver.responses.warehouse.ProductQuantityResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OfficeInventoryController {
    private final LogServerService logServerService;
    private final OfficeProfileRepository officeProfileRepository;
    private final EmployeeProfileService employeeProfileService;
    private final UserRepository userRepository;
    private final ProductQuantityRepository productQuantityRepository;
    private final OfficeProductRequestRepository officeProductRequestRepository;
    private final OfficeService officeService;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    public OfficeInventoryController(LogServerService logServerService,
                                     OfficeProfileRepository officeProfileRepository,
                                     EmployeeProfileService employeeProfileService,
                                     UserRepository userRepository,
                                     ProductQuantityRepository productQuantityRepository,
                                     OfficeProductRequestRepository officeProductRequestRepository,
                                     OfficeService officeService, WarehouseRepository warehouseRepository, ProductRepository productRepository) {
        this.logServerService = logServerService;
        this.officeProfileRepository = officeProfileRepository;
        this.employeeProfileService = employeeProfileService;
        this.userRepository = userRepository;
        this.productQuantityRepository = productQuantityRepository;
        this.officeProductRequestRepository = officeProductRequestRepository;
        this.officeService = officeService;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/merchant_dashboard/inventory_requests")
    @Secured("ROLE_MERCHANT")
    public ApiResponse sendRequestForProducts(
            @RequestBody OfficeInventoryRequest request,
            @CurrentUser UserPrincipal userPrincipal) {
        Optional<User> optionalUser = userRepository.findByUsername(userPrincipal.getUsername());
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }

        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(optionalUser.get());
        List<OfficeProfile> offices = officeProfileRepository.findAllByOfficeIdAndOffice_BusinessId(request.getOfficeId(), employeeProfile.getBusiness().getId());
        if (offices.size() == 0) {
            throw new AppException("Office does not exist!");
        }

        OfficeProductRequest officeProductRequest = officeProductRequestRepository.save(new OfficeProductRequest(request.getOfficeId()));
        List<ProductQuantity> productQuantities = request.getProducts().stream().map(productQuantityData -> {
            ProductQuantity response = new ProductQuantity();
            response.setProductId(productQuantityData.getId());
            response.setQuantity(productQuantityData.getQuantity());
            response.setOfficeProductRequest(officeProductRequest);
            return response;
        }).collect(Collectors.toList());
        productQuantityRepository.saveAll(productQuantities);

        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "products",
                                "request_products_to_office",
                                userPrincipal.getUsername() + " has requested products to office " +
                                        offices.get(0).getOffice().getContactInformation().getAddress() +
                                        " in " + offices.get(0).getOffice().getContactInformation().getCity()
                        )
                ),
                "warehouse"
        );
        return new ApiResponse("Successfully sent a request to the warehouse!", 201);
    }

    @GetMapping("/warehouse/requests")
    @Secured("ROLE_WAREMAN")
    public List<OfficeInventoryRequestResponse> getRequests(@CurrentUser UserPrincipal userPrincipal) {
        Optional<User> optionalUser = userRepository.findByUsername(userPrincipal.getUsername());
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(optionalUser.get());
        List<WarehouseResponse> warehouseResponses = warehouseRepository.findAllByBusiness(employeeProfile.getBusiness())
                .stream()
                .map(WarehouseResponse::new)
                .collect(Collectors.toList());
        List<Office> offices = officeService.findAllByBusiness(employeeProfile.getBusiness());
        List<OfficeProductRequest> requests = officeProductRequestRepository
                .findAll()
                .stream()
                .filter(
                    officeProductRequest -> offices.stream().anyMatch(office -> office.getId().equals(officeProductRequest.getOfficeId()))
                )
                .collect(Collectors.toList());
        try {
            return requests
                    .stream()
                    .map(
                            officeProductRequest -> {
                                ArrayList<ProductQuantityResponse> productQuantityResponses = productQuantityRepository
                                        .findAllByOfficeProductRequest_OfficeId(officeProductRequest.getOfficeId())
                                        .stream()
                                        .filter(
                                                productQuantity ->
                                                        productQuantity
                                                                .getOfficeProductRequest()
                                                                .getOfficeId()
                                                                .equals(officeProductRequest.getOfficeId())
                                        )
                                        .map(productQuantity -> {
                                            Optional<Product> optionalProduct = productRepository
                                                    .findById(productQuantity.getProductId());
                                            if (!optionalProduct.isPresent()) {
                                                throw new NullPointerException();
                                            }
                                            Product product = optionalProduct.get();
                                            ProductResponse productResponse = new ProductResponse(
                                                    product.getId(),
                                                    product.getName(),
                                                    product.getPrice(),
                                                    product.getPdv(),
                                                    null,
                                                    product.getUnit(),
                                                    product.getBarcode(),
                                                    product.getDescription(),
                                                    new DiscountResponse(
                                                            product.getDiscount()!=null?product.getDiscount().getPercentage():0
                                                    )
                                            );
                                            ArrayList<WarehouseResponse> warehouseResponses1 = warehouseResponses
                                                    .stream()
                                                    .filter(
                                                            warehouseResponse ->
                                                                    warehouseResponse
                                                                            .getProductId()
                                                                            .equals(product.getId())
                                                    )
                                                    .collect(Collectors.toCollection(ArrayList::new));
                                            double available = 0.0;
                                            if (!warehouseResponses1.isEmpty()) {
                                                available = warehouseResponses1.get(0).getQuantity();
                                            }
                                            return new ProductQuantityResponse(
                                                    productResponse,
                                                    productQuantity.getQuantity(),
                                                    available
                                            );
                                        }).collect(Collectors.toCollection(ArrayList::new));

                                return new OfficeInventoryRequestResponse(
                                        officeProductRequest.getId(),
                                        officeService.getOfficeResponseByOfficeId(officeProductRequest.getOfficeId()),
                                        productQuantityResponses
                                );
                            }
                    ).collect(Collectors.toList());
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    @PostMapping("/warehouse/requests/deny")
    @Secured("ROLE_WAREMAN")
    public ResponseEntity<ApiResponse> denyRequest(@RequestBody RequestAnswer requestAnswer) {
        System.out.println("The id: " + requestAnswer.getRequestId());
        System.out.println("The message: " + requestAnswer.getMessage());
        if (requestAnswer.getRequestId() == null) {
            throw new AppException("No id provided");
        }
        Optional<OfficeProductRequest> optionalOfficeProductRequest = officeProductRequestRepository
                .findById(requestAnswer.getRequestId());
        if (!optionalOfficeProductRequest.isPresent()) {
            throw new ResourceNotFoundException("That request does not exist");
        }
//        List<ProductQuantity> productQuantities = productQuantityRepository.findAllByOfficeProductRequest_OfficeId(optionalOfficeProductRequest.get().getOfficeId());
//        productQuantities = productQuantities.stream().filter(productQuantity -> productQuantity.getOfficeProductRequest().getId().equals(optionalOfficeProductRequest.get().getId())).collect(Collectors.toCollection(ArrayList::new));
//        productQuantityRepository.deleteAll(productQuantities);
        officeProductRequestRepository.delete(optionalOfficeProductRequest.get());
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "warning",
                        new NotificationPayload(
                                "request",
                                "request_deny",
                                requestAnswer.getMessage()
                        )
                ),
                "merchant_dashboard"
        );
        return ResponseEntity.ok(new ApiResponse("Successfully denied request!", 200));
    }

    @PostMapping("/warehouse/requests/accept")
    @Secured("ROLE_WAREMAN")
    public ResponseEntity<ApiResponse> acceptRequest(@RequestBody RequestAnswer requestAnswer) {
        System.out.println("The id: " + requestAnswer.getRequestId());
        System.out.println("The message: " + requestAnswer.getMessage());
        if (requestAnswer.getRequestId() == null) {
            throw new AppException("No id provided");
        }
        Optional<OfficeProductRequest> optionalOfficeProductRequest = officeProductRequestRepository
                .findById(requestAnswer.getRequestId());
        if (!optionalOfficeProductRequest.isPresent()) {
            throw new ResourceNotFoundException("That request does not exist");
        }
//        List<ProductQuantity> productQuantities = productQuantityRepository.findAllByOfficeProductRequest_OfficeId(optionalOfficeProductRequest.get().getOfficeId());
//        productQuantities = productQuantities.stream().filter(productQuantity -> productQuantity.getOfficeProductRequest().getId().equals(optionalOfficeProductRequest.get().getId())).collect(Collectors.toCollection(ArrayList::new));
//        productQuantityRepository.deleteAll(productQuantities);
        officeProductRequestRepository.delete(optionalOfficeProductRequest.get());
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "request",
                                "request_deny",
                                "request accepted!"
                        )
                ),
                "merchant_dashboard"
        );
        return ResponseEntity.ok(new ApiResponse("Successfully accepted request!", 200));
    }
}
