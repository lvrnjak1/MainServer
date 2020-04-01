package ba.unsa.etf.si.mainserver.models.transactions;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Data
@Entity
@Table(name = "receipt_statuses")
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptStatus extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    private ReceiptStatusName statusName;
}
