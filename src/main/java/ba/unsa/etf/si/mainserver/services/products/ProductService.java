package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.models.products.items.ProductItem;
import ba.unsa.etf.si.mainserver.repositories.auth.UserRepository;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.products.ProductRepository;
import ba.unsa.etf.si.mainserver.responses.products.ExtendedProductResponse;
import ba.unsa.etf.si.mainserver.responses.products.ProductResponse;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
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
    private final UserRepository userRepository;
    private final ItemService itemService;

    public ProductService(ProductRepository productRepository,
                          BusinessRepository businessRepository,
                          EmployeeProfileService employeeProfileService,
                          UserRepository userRepository,
                          ItemService itemService) {
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
        this.employeeProfileService = employeeProfileService;
        this.userRepository = userRepository;
        this.itemService = itemService;
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
        Optional<User> optionalUser = userRepository.findByUsername(userPrincipal.getUsername());
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User does not exist");
        }
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(optionalUser.get());
        return productRepository.findAllByBusinessId(employeeProfile.getBusiness().getId())
                .stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    public List<ExtendedProductResponse> getAllProductResponsesWithBusiness() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> {
            List<ProductItem> productItems = itemService.findAllProductItems(product);
            return new ExtendedProductResponse(product, productItems);
        }).collect(Collectors.toList());
    }

    public List<ExtendedProductResponse> getAllProductOnSaleResponsesWithBusiness() {
        List<Product> products = productRepository.findAll();
        return products
                .stream()
                .filter(product -> product.getDiscount() != null && product.getDiscount().getPercentage() > 0)
                .map(ExtendedProductResponse::new)
                .collect(Collectors.toList());
    }

    public Product findProductById(Long productId, Long businessId) {
        Optional<Product> optionalProduct = findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product does not exist");
        }
        Product product = optionalProduct.get();
        if (!product.getBusiness().getId().equals(businessId)) {
            throw new UnauthorizedException("Not your product");
        }
        return optionalProduct.get();
    }


    public List<Product> findProductItemsByType(Long typeId) {
        return productRepository.findAllByItemType_Id(typeId);
    }
}
