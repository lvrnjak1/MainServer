package ba.unsa.etf.si.mainserver.responses.pr;

import ba.unsa.etf.si.mainserver.models.business.Office;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeResponseLite {
    private Long id;
    private String address;
    private String city;
    private String country;
    private String email;
    private String phoneNumber;
    private String workDayStart;
    private String workDayEnd;
    private Long businessId;
    private String businessName;
    private ManagerResponseLite manager;

    public OfficeResponseLite(Office office){
        this.id = office.getId();
        this.address = office.getContactInformation().getAddress();
        this.city = office.getContactInformation().getCity();
        this.country = office.getContactInformation().getCountry();
        this.email = office.getContactInformation().getEmail();
        this.phoneNumber = office.getContactInformation().getPhoneNumber();
        this.workDayStart = office.getStringStart();
        this.workDayEnd = office.getStringEnd();
        this.manager = new ManagerResponseLite();
        if(office.getManager() != null){
            this.manager = new ManagerResponseLite(office.getManager());
        }
        this.businessId = office.getBusiness().getId();
        this.businessName = office.getBusiness().getName();
    }
}
