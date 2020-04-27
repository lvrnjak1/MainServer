package ba.unsa.etf.si.mainserver.models.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@javax.persistence.Table(name = "tables")
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private int tableNumber;

    @ManyToOne
    private Office office;

    public Table(int tableNumber, Office office){
        this.tableNumber = tableNumber;
        this.office = office;
    }
}
