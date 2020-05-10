package ba.unsa.etf.si.mainserver.responses.products.items;

import ba.unsa.etf.si.mainserver.models.products.items.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private String unit;

    public ItemResponse(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.unit = item.getUnit();
    }
}
