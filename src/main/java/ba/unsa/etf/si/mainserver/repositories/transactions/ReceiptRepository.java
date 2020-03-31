package ba.unsa.etf.si.mainserver.repositories.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
