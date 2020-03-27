package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.models.products.Discount;
import ba.unsa.etf.si.mainserver.repositories.products.DiscountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DiscountService {
    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public Optional<Discount> findById(Long id) {
        return discountRepository.findById(id);
    }

    public Discount save(Discount discount) {
        return discountRepository.save(discount);
    }
}
