package ba.unsa.etf.si.mainserver.models.products.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

//jedan element liste sastojaka (npr paprika)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String name;
    private String unit;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ItemType itemType;
}
