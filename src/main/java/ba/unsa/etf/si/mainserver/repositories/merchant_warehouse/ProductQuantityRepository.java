package ba.unsa.etf.si.mainserver.repositories.merchant_warehouse;

import ba.unsa.etf.si.mainserver.models.merchant_warehouse.ProductQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductQuantityRepository extends JpaRepository<ProductQuantity,Long> {
    List<ProductQuantity> findAllByOfficeProductRequest_OfficeId(Long officeId);
}
