package ba.unsa.etf.si.mainserver.models.pr;

import ba.unsa.etf.si.mainserver.models.business.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@javax.persistence.Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private ReservationStatus reservationStatus;

    @ManyToOne
    private Table table;

    private String name;
    private String surname;
    private String email;

    private LocalDateTime reservationDateTime;

    private Long verificationCode;

    public Reservation(ReservationStatus reservationStatus, Table table,
                       String name,
                       String surname,
                       String email,
                       LocalDateTime reservationDateTime,
                       Long verificationCode){
        this.reservationStatus = reservationStatus;
        this.table = table;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.reservationDateTime = reservationDateTime;
        this.verificationCode = verificationCode;
    }
}
