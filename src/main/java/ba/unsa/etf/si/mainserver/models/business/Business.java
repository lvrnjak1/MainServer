package ba.unsa.etf.si.mainserver.models.business;

import ba.unsa.etf.si.mainserver.models.AuditModel;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private EmployeeProfile merchant;

    @Column(name = "main_office_id")
    private Long mainOfficeId = null;

    private int maxNumberOffices = 5;

    @Basic
    @Temporal(TemporalType.TIME)
    private Date syncTime;

    {
        try {
            syncTime = new SimpleDateFormat("HH:mm").parse("06:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Column(name = "reservation_duration")
    private Long reservationDurationMins = 60L;

    private String placeName = "Tables";


    public Business(String name, boolean restaurantFeature, EmployeeProfile merchant) {
        this.name = name;
        this.restaurantFeature = restaurantFeature;
        this.merchant = merchant;
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

    public EmployeeProfile getMerchant() {
        return merchant;
    }

    public void setMerchant(EmployeeProfile merchant) {
        this.merchant = merchant;
    }

    public Long getMainOfficeId() {
        return mainOfficeId;
    }

    public void setMainOfficeId(Long mainOfficeId) {
        this.mainOfficeId = mainOfficeId;
    }

    public int getMaxNumberOffices() {
        return maxNumberOffices;
    }

    public void setMaxNumberOffices(int max_number_offices) {
        this.maxNumberOffices = max_number_offices;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public String getStringSyncDate(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(getSyncTime());
    }

    public Long getReservationDurationMins() {
        return reservationDurationMins;
    }

    public void setReservationDurationMins(Long reservationDurationMins) {
        this.reservationDurationMins = reservationDurationMins;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
