package ba.unsa.etf.si.mainserver.models.transactions;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reciepts")
public class Receipt extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String receiptId;
    private Long cashRegisterId;
    private Long officeId;
    private Long businessId;
    private String employeeUsername;
    private BigDecimal total;
    private ReceiptStatus status;

    @Basic
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToMany
    @JoinTable(name = "reciept_recieptItem",
            joinColumns = @JoinColumn(name = "reciept_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private Set<ReceiptItem> receiptItems;

}
