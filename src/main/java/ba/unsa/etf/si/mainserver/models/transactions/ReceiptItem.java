package ba.unsa.etf.si.mainserver.models.transactions;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "receipt_items")
public class ReceiptItem extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long productId;
    private double quantity;
}
