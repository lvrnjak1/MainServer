package ba.unsa.etf.si.mainserver.repositories.products;

import ba.unsa.etf.si.mainserver.models.business.AdminMerchantNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMerchantNotificationRepository extends JpaRepository<AdminMerchantNotification, Long> {
}
