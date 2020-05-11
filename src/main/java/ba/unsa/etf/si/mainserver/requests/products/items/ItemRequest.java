package ba.unsa.etf.si.mainserver.requests.products.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private String name;
    private String unit;
    private Long itemTypeId;
}
