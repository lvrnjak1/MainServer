package ba.unsa.etf.si.mainserver.models.products;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id")
    private Product product;
    String firstName;
    String lastName;
    String email;
    String text;


    public Comment(Product product, String firstName, String lastName, String email, String text) {
        this.product = product;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.text = text;
    }


}
