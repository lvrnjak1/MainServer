package ba.unsa.etf.si.mainserver.models.transactions;

import ba.unsa.etf.si.mainserver.models.AuditModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

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

    public Receipt(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public Long getCashRegisterId() {
        return cashRegisterId;
    }

    public void setCashRegisterId(Long cashRegisterId) {
        this.cashRegisterId = cashRegisterId;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ReceiptStatus getStatus() {
        return status;
    }

    public void setStatus(ReceiptStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Set<ReceiptItem> getReceiptItems() {
        return receiptItems;
    }

    public void setReceiptItems(Set<ReceiptItem> receiptItems) {
        this.receiptItems = receiptItems;
    }
}
