package ba.unsa.etf.si.mainserver.repositories.products;

import ba.unsa.etf.si.mainserver.models.business.AdminMerchantNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminMerchantNotificationRepository extends JpaRepository<AdminMerchantNotification, Long> {
    Optional<AdminMerchantNotification> findByOffice_AddressAndOffice_CityAndOffice_CountryAndOffice_EmailAndOffice_PhoneNumber(
            String address, String city, String country, String email, String phoneNumber
    );
}
