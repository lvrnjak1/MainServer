package ba.unsa.etf.si.mainserver.models.products;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouse_logs")
public class WarehouseLog extends AuditModel {
    //tabela koja sluzi da se zapise kada u skladiste
    //dodje odredjena kolicina nekog proizvoda
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    private double quantity;

    public WarehouseLog(Product product, double quantity){
        this.product = product;
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
