package ba.unsa.etf.si.mainserver.controllers.merchant_dashboard;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.merchant_warehouse.OfficeProductRequest;
import ba.unsa.etf.si.mainserver.models.merchant_warehouse.ProductQuantity;
import ba.unsa.etf.si.mainserver.models.products.Product;
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
import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import ba.unsa.etf.si.mainserver.responses.products.WarehouseResponse;
import ba.unsa.etf.si.mainserver.responses.warehouse.OfficeInventoryRequestResponse;
import ba.unsa.etf.si.mainserver.responses.warehouse.ProductQuantityResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.products.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OfficeInventoryController {
    private final LogServerService logServerService;
    private final OfficeProfileRepository officeProfileRepository;
    private final EmployeeProfileService employeeProfileService;
    private final UserService userService;
    private final ProductQuantityRepository productQuantityRepository;
    private final OfficeProductRequestRepository officeProductRequestRepository;
    private final OfficeService officeService;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OfficeInventoryController(LogServerService logServerService,
                                     OfficeProfileRepository officeProfileRepository,
                                     EmployeeProfileService employeeProfileService,
                                     UserService userService,
                                     ProductQuantityRepository productQuantityRepository,
                                     OfficeProductRequestRepository officeProductRequestRepository,
                                     OfficeService officeService, WarehouseRepository warehouseRepository, ProductRepository productRepository, ProductService productService) {
        this.logServerService = logServerService;
        this.officeProfileRepository = officeProfileRepository;
        this.employeeProfileService = employeeProfileService;
        this.userService = userService;
        this.productQuantityRepository = productQuantityRepository;
        this.officeProductRequestRepository = officeProductRequestRepository;
        this.officeService = officeService;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    //ovo je ok
    @PostMapping("/merchant_dashboard/inventory_requests")
    @Secured("ROLE_MERCHANT")
    public ApiResponse sendRequestForProducts(
            @RequestBody OfficeInventoryRequest request,
            @CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findUserByUsername(userPrincipal.getUsername());
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);
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
        User user = userService.findUserByUsername(userPrincipal.getUsername());
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);

        List<Office> offices = officeService.findAllByBusiness(employeeProfile.getBusiness());
        List<OfficeProductRequest> requests = officeProductRequestRepository
                .findAll()
                .stream()
                .filter(
                    officeProductRequest -> offices.stream().anyMatch(office -> office.getId().equals(officeProductRequest.getOfficeId()))
                )
                .collect(Collectors.toList());

        return requests
                .stream()
                .map(request -> getRequestProductsResponse(request, employeeProfile.getBusiness()))
                .collect(Collectors.toList());
    }

    private OfficeInventoryRequestResponse getRequestProductsResponse(OfficeProductRequest officeProductRequest, Business business){

        ArrayList<ProductQuantityResponse> productQuantityResponses = productQuantityRepository
                .findAllByOfficeProductRequest_OfficeId(officeProductRequest.getOfficeId())
                .stream()
                .filter(
                        productQuantity ->
                                productQuantity.getOfficeProductRequest().getId().equals(officeProductRequest.getId())
                )
                .map(productQuantity -> {
                    Product product = productService
                            .findProductById(productQuantity.getProductId(), business.getId());
                    return new ProductQuantityResponse(new ProductResponse(product), productQuantity.getQuantity(), getProductAvailableQuantity(product, business));
                })
                .collect(Collectors.toCollection(ArrayList::new));

        return new OfficeInventoryRequestResponse(
                officeProductRequest.getId(),
                officeService.getOfficeResponseByOfficeId(officeProductRequest.getOfficeId()),
                productQuantityResponses
        );
    }

    private double getProductAvailableQuantity(Product product, Business business){
        ArrayList<WarehouseResponse> warehouseResponses = warehouseRepository.findAllByBusiness(business)
                .stream()
                .map(WarehouseResponse::new)
                .filter(
                        warehouseResponse ->
                                warehouseResponse
                                        .getProductId()
                                        .equals(product.getId())
                )
                .collect(Collectors.toCollection(ArrayList::new));
        double available = 0.0;
        if (!warehouseResponses.isEmpty()) {
            available = warehouseResponses.get(0).getQuantity();
        }
        return available;
    }

    @PostMapping("/warehouse/requests/deny")
    @Secured("ROLE_WAREMAN")
    public ResponseEntity<ApiResponse> denyRequest(@RequestBody RequestAnswer requestAnswer) {
        if (requestAnswer.getRequestId() == null) {
            throw new AppException("No id provided");
        }
        OfficeProductRequest officeProductRequest = officeProductRequestRepository.findById(requestAnswer.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("That request does not exist"));

        List<ProductQuantity> productQuantities = productQuantityRepository
                .findAllByOfficeProductRequest_OfficeId(officeProductRequest.getOfficeId())
                .stream()
                .filter(productQuantity ->
                        productQuantity.getOfficeProductRequest().getId()
                                .equals(officeProductRequest.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        productQuantityRepository.deleteAll(productQuantities);
        officeProductRequestRepository.delete(officeProductRequest);
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
        return ResponseEntity.ok(new ApiResponse("The request for products has been denied!", 200));
    }

    @PostMapping("/warehouse/requests/accept")
    @Secured("ROLE_WAREMAN")
    public ResponseEntity<ApiResponse> acceptRequest(@RequestBody RequestAnswer requestAnswer) {
        if (requestAnswer.getRequestId() == null) {
            throw new AppException("No id provided");
        }
        OfficeProductRequest officeProductRequest = officeProductRequestRepository.findById(requestAnswer.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("That request does not exist"));

        List<ProductQuantity> productQuantities = productQuantityRepository
                .findAllByOfficeProductRequest_OfficeId(officeProductRequest.getOfficeId())
                .stream()
                .filter(productQuantity ->
                        productQuantity.getOfficeProductRequest().getId()
                                .equals(officeProductRequest.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        productQuantityRepository.deleteAll(productQuantities);
        officeProductRequestRepository.delete(officeProductRequest);
//        logServerService.broadcastNotification(
//                new NotificationRequest(
//                        "info",
//                        new NotificationPayload(
//                                "request",
//                                "request_deny",
//                                "request accepted!"
//                        )
//                ),
//                "merchant_dashboard"
//        );
        return ResponseEntity.ok(new ApiResponse("The request for products has been accepted!", 200));
    }
}
