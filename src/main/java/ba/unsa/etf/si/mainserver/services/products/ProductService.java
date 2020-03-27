package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.products.ProductRepository;
import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;
    private final EmployeeProfileService employeeProfileService;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, BusinessRepository businessRepository, EmployeeProfileService employeeProfileService, UserService userService) {
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
        this.employeeProfileService = employeeProfileService;
        this.userService = userService;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public List<Product> findByBusiness(Business business) {
        return productRepository.findByBusiness(business);
    }

    public List<Product> findByBusinessId(Long businessId) {
        if (!businessRepository.existsById(businessId)) {
            throw new ResourceNotFoundException("Business with id " + businessId + " not found!");
        }
        return productRepository.findAllByBusinessId(businessId);
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public List<ProductResponse> findAllProductResponsesForCurrentUser(UserPrincipal userPrincipal) {
        Optional<User> optionalUser = userService.findByUsername(userPrincipal.getUsername());
        if (!optionalUser.isPresent()) {
            throw new AppException("You did some nasty things!");
        }
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileService.findByAccount(optionalUser.get());
        if (!optionalEmployeeProfile.isPresent()) {
            throw new AppException("Well congrats, you killed the server");
        }
        return productRepository.findAllByBusinessId(optionalEmployeeProfile.get().getBusiness().getId())
                .stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }
}
