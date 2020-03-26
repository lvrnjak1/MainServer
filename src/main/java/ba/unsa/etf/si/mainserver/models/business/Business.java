package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.products.Product;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "businesses")
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
    @JsonManagedReference
    private Set<Office> offices;

    @OneToMany(mappedBy="business")
    @JsonManagedReference
    private Set<Product> products;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private EmployeeProfile merchant;

    public Business(String name, boolean restaurantFeature, EmployeeProfile merchant) {
        this.name = name;
        this.restaurantFeature = restaurantFeature;
        this.merchant = merchant;
        this.offices = new HashSet<>(); //???
        this.products = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRestaurantFeature() {
        return restaurantFeature;
    }

    public void setRestaurantFeature(boolean restaurantFeature) {
        this.restaurantFeature = restaurantFeature;
    }

    public Set<Office> getOffices() {
        return offices;
    }

    public void setOffices(Set<Office> offices) {
        this.offices = offices;
    }

    public EmployeeProfile getMerchant() {
        return merchant;
    }

    public void setMerchant(EmployeeProfile merchant) {
        this.merchant = merchant;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
