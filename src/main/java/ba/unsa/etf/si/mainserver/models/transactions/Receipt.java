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
    private String username;
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private ReceiptStatus status;

    @ManyToOne
    @JoinColumn(name = "method_id")
    private PaymentMethod paymentMethod;

    @Basic
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timestamp;

    @OneToMany
    @JoinColumn(name = "receipt_id")
    private Set<ReceiptItem> receiptItems;

    public Receipt(String receiptId, Long cashRegisterId, Long officeId, Long businessId, String username,
                   BigDecimal totalPrice, ReceiptStatus receiptStatus, PaymentMethod paymentMethod,
                   Date timestamp, Set<ReceiptItem> receiptItems){
        this.receiptId = receiptId;
        this.cashRegisterId = cashRegisterId;
        this.officeId = officeId;
        this.businessId = businessId;
        this.username = username;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.status = receiptStatus;
        this.timestamp = timestamp;
        this.receiptItems = receiptItems;
    }

}
