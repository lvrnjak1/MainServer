package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.products.InventoryLog;
import ba.unsa.etf.si.mainserver.models.products.OfficeInventory;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.repositories.products.InventoryLogRepostory;
import ba.unsa.etf.si.mainserver.repositories.products.OfficeInventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfficeInventoryService {
    private final OfficeInventoryRepository officeInventoryRepository;
    private final InventoryLogRepostory inventoryLogRepostory;

    public OfficeInventoryService(OfficeInventoryRepository officeInventoryRepository,
                                  InventoryLogRepostory inventoryLogRepostory){
        this.officeInventoryRepository = officeInventoryRepository;
        this.inventoryLogRepostory = inventoryLogRepostory;
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

    public List<OfficeInventory> findByProduct(Product product) {
        return officeInventoryRepository.findAllByProduct(product);
    }

    public void delete(OfficeInventory officeInventory) {
        officeInventoryRepository.delete(officeInventory);
    }

    public void logDelivery(OfficeInventory officeInventory, double quantity) {
        InventoryLog inventoryLog = new InventoryLog(officeInventory.getProduct(),
                officeInventory.getOffice(),
                quantity);

        inventoryLogRepostory.save(inventoryLog);
    }

    public List<InventoryLog> findAllByBusiness(Business business) {
        return inventoryLogRepostory.findAllByProduct_Business(business);
    }
}
