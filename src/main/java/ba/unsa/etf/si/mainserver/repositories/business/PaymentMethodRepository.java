package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.transactions.PaymentMethod;
import ba.unsa.etf.si.mainserver.models.transactions.PaymentMethodName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByMethodName(PaymentMethodName name);
}
