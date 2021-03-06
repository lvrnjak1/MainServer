package ba.unsa.etf.si.mainserver.repositories.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {
    List<ReceiptItem> findAllByProductId(Long productId);
}
