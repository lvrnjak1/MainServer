package ba.unsa.etf.si.mainserver.responses.business;

import ba.unsa.etf.si.mainserver.models.business.Office;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeResponse {
    private Long id;
    private String address;
    private String city;
    private String country;
    private String email;
    private String phoneNumber;
    private String workDayStart;
    private String workDayEnd;
    private List<CashRegisterResponse> cashRegisters;
    private EmployeeProfileResponse manager;

    public OfficeResponse(Office office, List<CashRegisterResponse> cashRegisters){
        this.id = office.getId();
        this.address = office.getContactInformation().getAddress();
        this.city = office.getContactInformation().getCity();
        this.country = office.getContactInformation().getCountry();
        this.email = office.getContactInformation().getEmail();
        this.phoneNumber = office.getContactInformation().getPhoneNumber();
        this.workDayStart = office.getStringStart();
        this.workDayEnd = office.getStringEnd();
        this.cashRegisters = cashRegisters;
        this.manager = new EmployeeProfileResponse();
        if(office.getManager() != null){
            this.manager = new EmployeeProfileResponse(office.getManager());
        }
    }
}
