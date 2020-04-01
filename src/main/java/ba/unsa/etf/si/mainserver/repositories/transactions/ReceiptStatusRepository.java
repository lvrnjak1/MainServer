package ba.unsa.etf.si.mainserver.repositories.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.ReceiptStatus;
import ba.unsa.etf.si.mainserver.models.transactions.ReceiptStatusName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptStatusRepository extends JpaRepository<ReceiptStatus, Long> {
    Optional<ReceiptStatus> findByStatusName(ReceiptStatusName receiptStatusName);
}
