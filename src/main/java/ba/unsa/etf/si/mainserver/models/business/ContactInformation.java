package ba.unsa.etf.si.mainserver.models.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "contact_information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;
}
