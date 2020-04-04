package ba.unsa.etf.si.mainserver.repositories.transactions;

import ba.unsa.etf.si.mainserver.models.transactions.Receipt;
import ba.unsa.etf.si.mainserver.models.transactions.ReceiptStatusName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByReceiptId(String id);
    Optional<Receipt> findByBusinessIdAndCashRegisterIdAndOfficeIdAndStatus_StatusName(
            Long businessId, Long cashRegisterId, Long officeId, ReceiptStatusName statusName);
    List<Receipt> findAllByCashRegisterIdAndStatus_StatusName(Long cashRegister, ReceiptStatusName name);
    List<Receipt> findAllByUsernameAndStatus_StatusName(String username, ReceiptStatusName name);
}
