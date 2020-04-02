package ba.unsa.etf.si.mainserver.models.products;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.business.Office;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inventory_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryLog extends AuditModel {
    //tabela koja sluzi da se evidentira kada se u neku poslovnicu prebaci odredjena
    //kolicina artikala
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "office_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Office office;

    private double quantity;

    public InventoryLog(Product product, Office office, double quantity){
        this.product = product;
        this.office = office;
        this.quantity = quantity;
    }

    public String getStringDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
        return dateFormat.format(getCreatedAt());
    }

    public String getStringTime(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(getCreatedAt());
    }
}
