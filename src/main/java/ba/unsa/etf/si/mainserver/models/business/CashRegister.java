package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "cash_registers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashRegister extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="office_id", nullable=false)
    private Office office;

    public CashRegister(Office office){
        this.office = office;
    }

    //staticki QR kod ce biti samo ova tri podatka zakodirana
    //to oni trebaju iz ova tri podatka generisati
    //id, officeId, businessId
}
