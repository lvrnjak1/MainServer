package ba.unsa.etf.si.mainserver.repositories.merchant_warehouse;

import ba.unsa.etf.si.mainserver.models.merchant_warehouse.OfficeProductRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeProductRequestRepository extends JpaRepository<OfficeProductRequest,Long> {
}
