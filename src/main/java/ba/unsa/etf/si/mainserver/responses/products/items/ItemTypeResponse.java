package ba.unsa.etf.si.mainserver.responses.products.items;

import ba.unsa.etf.si.mainserver.models.products.items.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemTypeResponse {
    private Long id;
    private String name;

    public ItemTypeResponse(ItemType itemType){
        this.id = itemType.getId();
        this.name = itemType.getName();
    }
}
