package ba.unsa.etf.si.mainserver.models.products.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

//govori sta predstavljaju elementi liste
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_types")
public class ItemType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String name;
    private Long businessId;

    public ItemType(String name, Long businessId) {
        this.name = name;
        this.businessId = businessId;
    }
}
