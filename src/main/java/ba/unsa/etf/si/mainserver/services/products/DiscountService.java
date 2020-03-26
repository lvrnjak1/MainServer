package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.repositories.products.DiscountRepository;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {
    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }
}
