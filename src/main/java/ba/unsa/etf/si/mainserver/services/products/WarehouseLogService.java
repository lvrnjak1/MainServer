package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.models.products.Warehouse;
import ba.unsa.etf.si.mainserver.models.products.WarehouseLog;
import ba.unsa.etf.si.mainserver.repositories.products.WarehouseLogRepository;
import org.springframework.stereotype.Service;

@Service
public class WarehouseLogService {
    private final WarehouseLogRepository warehouseLogRepository;

    public WarehouseLogService(WarehouseLogRepository warehouseLogRepository) {
        this.warehouseLogRepository = warehouseLogRepository;
    }

    public void logNewDelivery(Warehouse warehouse, double quantity) {
        WarehouseLog warehouseLog = new WarehouseLog(warehouse.getProduct(),
                quantity);

        warehouseLogRepository.save(warehouseLog);
    }
}
