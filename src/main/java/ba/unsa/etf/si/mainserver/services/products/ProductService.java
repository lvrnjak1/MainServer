package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.repositories.products.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
