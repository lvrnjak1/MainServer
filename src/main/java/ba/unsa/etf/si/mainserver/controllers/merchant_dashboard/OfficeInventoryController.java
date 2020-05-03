package ba.unsa.etf.si.mainserver.controllers.merchant_dashboard;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.merchant_warehouse.OfficeProductRequest;
import ba.unsa.etf.si.mainserver.models.merchant_warehouse.ProductQuantity;
import ba.unsa.etf.si.mainserver.repositories.auth.UserRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.merchant_warehouse.OfficeProductRequestRepository;
import ba.unsa.etf.si.mainserver.repositories.merchant_warehouse.ProductQuantityRepository;
import ba.unsa.etf.si.mainserver.requests.merchant_dashboard.OfficeInventoryRequest;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationPayload;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.business.OfficeResponse;
import ba.unsa.etf.si.mainserver.responses.warehouse.OfficeInventoryRequestResponse;
import ba.unsa.etf.si.mainserver.responses.warehouse.ProductQuantityResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
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

    public OfficeInventoryController(LogServerService logServerService,
                                     OfficeProfileRepository officeProfileRepository,
                                     EmployeeProfileService employeeProfileService,
                                     UserRepository userRepository,
                                     ProductQuantityRepository productQuantityRepository,
                                     OfficeProductRequestRepository officeProductRequestRepository,
                                     OfficeService officeService) {
        this.logServerService = logServerService;
        this.officeProfileRepository = officeProfileRepository;
        this.employeeProfileService = employeeProfileService;
        this.userRepository = userRepository;
        this.productQuantityRepository = productQuantityRepository;
        this.officeProductRequestRepository = officeProductRequestRepository;
        this.officeService = officeService;
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
                .map(
                        officeProductRequest -> {
                            ArrayList<ProductQuantityResponse> productQuantityResponses = new ArrayList<>();

                            return new OfficeInventoryRequestResponse(
                                    officeProductRequest.getId(),
                                    officeService.getOfficeResponseByOfficeId(officeProductRequest.getOfficeId()),
                                    productQuantityResponses
                            );
                        }
                ).collect(Collectors.toList());

    }
}
