package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "cash_registers")
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

    private String name;

    public CashRegister(Office office){
        this.office = office;
    }

    public CashRegister(Office office, String name){
        this.office = office;
        this.name = name;
    }

    //staticki QR kod ce biti samo ova tri podatka zakodirana
    //to oni trebaju iz ova tri podatka generisati
    //id, officeId, businessId

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
