package ba.unsa.etf.si.mainserver.services.products;

import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.products.InventoryLog;
import ba.unsa.etf.si.mainserver.models.products.Product;
import ba.unsa.etf.si.mainserver.repositories.products.InventoryLogRepostory;
import ba.unsa.etf.si.mainserver.responses.products.ShippingResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryLogService {
    private final InventoryLogRepostory inventoryLogRepostory;
    private final ProductService productService;

    public InventoryLogService(InventoryLogRepostory inventoryLogRepostory, ProductService productService) {
        this.inventoryLogRepostory = inventoryLogRepostory;
        this.productService = productService;
    }

    public List<ShippingResponse> getShippingLogForBusiness(Business business) {
        return productService.findByBusiness(business)
                .stream()
                .map(this::getShippingForProduct)
                .filter(shippingResponse -> shippingResponse.getQuantity() != 0)
                .sorted(Comparator.comparingDouble(ShippingResponse::getQuantity).reversed())
                .collect(Collectors.toList());
    }

    private ShippingResponse getShippingForProduct(Product product) {
        double quantity = inventoryLogRepostory.findAllByProduct(product)
                .stream()
                .mapToDouble(InventoryLog::getQuantity)
                .sum();
        return new ShippingResponse(product.getId(), product.getName(), quantity);
    }
}
