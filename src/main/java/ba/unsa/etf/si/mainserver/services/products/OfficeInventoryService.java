package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.repositories.products.OfficeInventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfficeInventoryService {
    private final OfficeInventoryRepository officeInventoryRepository;

    public OfficeInventoryService(OfficeInventoryRepository officeInventoryRepository ){
        this.officeInventoryRepository = officeInventoryRepository;
    }

    public List<OfficeInventory> findAllProductsForOffice(Office office) {
        return officeInventoryRepository.findAllByOffice(office);
    }

    public OfficeInventory save(OfficeInventory officeInventory) {
        return officeInventoryRepository.save(officeInventory);
    }

    public Optional<OfficeInventory> findByProductAndOffice(Product product, Office office) {
        return officeInventoryRepository.findByProductAndOffice(product, office);
    }
}
