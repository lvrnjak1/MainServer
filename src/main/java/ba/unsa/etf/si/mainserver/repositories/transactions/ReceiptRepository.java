package ba.unsa.etf.si.mainserver.repositories.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByReceiptId(String id);
    Optional<Receipt> findByBusinessIdAndCashRegisterIdAndOfficeId(Long businessId, Long cashRegisterId, Long officeId);
}
