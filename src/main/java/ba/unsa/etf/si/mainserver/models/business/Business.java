package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "businesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Business extends AuditModel { //ovo je kao Bingo
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String name;
    private boolean restaurantFeature = false;

    @OneToMany(mappedBy="business")
    private Set<Office> offices;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private EmployeeProfile merchant;

    public Business(String name, boolean restaurantFeature, EmployeeProfile merchant) {
        this.name = name;
        this.restaurantFeature = restaurantFeature;
        this.merchant = merchant;
        this.offices = new HashSet<>(); //???
    }
}
