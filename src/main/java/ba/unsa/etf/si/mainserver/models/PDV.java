package ba.unsa.etf.si.mainserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "pdv_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDV {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double pdvRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPdvRate() {
        return pdvRate;
    }

    public void setPdvRate(double pdv_rate) {
        this.pdvRate = pdv_rate;
    }

    public PDV (double pdv_rate){
        this.pdvRate = pdv_rate;
    }
}