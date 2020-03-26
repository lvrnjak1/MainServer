package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.EmployeeProfile;
import ba.unsa.etf.si.mainserver.requests.business.BusinessRequest;
import ba.unsa.etf.si.mainserver.requests.business.EmployeeProfileRequest;
import ba.unsa.etf.si.mainserver.requests.business.OfficeRequest;
import ba.unsa.etf.si.mainserver.responses.business.BusinessResponse;
import ba.unsa.etf.si.mainserver.responses.business.EmployeeProfileResponse;
import ba.unsa.etf.si.mainserver.responses.business.OfficeResponse;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    //TODO
    //provjeriti koje sve role mogu pristupati kojim rutama

    private final BusinessService businessService;
    private final EmployeeProfileService employeeProfileService;

    public BusinessController(BusinessService businessService, EmployeeProfileService employeeProfileService) {
        this.businessService = businessService;
        this.employeeProfileService = employeeProfileService;
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public BusinessResponse registerNewBusiness(@RequestBody BusinessRequest businessRequest){
        Optional<EmployeeProfile> employeeProfileOptional = employeeProfileService.findById(businessRequest.getMerchantId());
        if(employeeProfileOptional.isPresent()) {
            Business business = new Business(businessRequest.getName(), businessRequest.isRestaurantFeature(), employeeProfileOptional.get());
            return new BusinessResponse(businessService.save(business));
        }

        throw new AppException("Employee with id " + businessRequest.getMerchantId() + " doesn't exist");
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    public List<BusinessResponse> getAllBusinesses(){
        return businessService.findAll().stream().map(BusinessResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public BusinessResponse getBusinessById(@PathVariable("id") Long businessId){
        return null;
    }

    @GetMapping("/{id}/offices")
    @Secured("ROLE_ADMIN")
    public List<OfficeResponse> getAllOfficesForBusiness(@PathVariable("id") Long businessId){
        return null;
    }

    @PostMapping("/{id}/restaurant")
    @Secured("ROLE_ADMIN")
    public BusinessResponse toggleRestaurantFeature(@PathVariable("id") Long businessId){
        return null;
    }

    @PostMapping("/{id}/offices")
    @Secured("ROLE_ADMIN")
    public BusinessResponse addOffice(@PathVariable("id") Long businessId,
                                      @RequestBody OfficeRequest officeRequest){
        return null;
    }

    @DeleteMapping("/{businessId}/offices/{officeId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteOffice(@PathVariable("businessId") Long businessId,
                                       @PathVariable("officeId") Long officeId){
        return null;
    }

    @PostMapping("/offices/{officeId}/cashRegisters")
    @Secured("ROLE_ADMIN")
    public OfficeResponse addCashRegisterForOffice(@PathVariable("officeId") Long officeId){
        return null;
    }

    @DeleteMapping("/{businessId}/offices/{officeId}/cashRegisters/{cashRegId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteOffice(@PathVariable("businessId") Long businessId,
                                          @PathVariable("officeId") Long officeId,
                                          @PathVariable("cashRegId") Long cashRegisterId){
        return null;
    }


    //ovo nije dobra ruta treba napravit fino ovo je samo pomocna za testiranje
    //TODO
    @PostMapping("/employees")
    @Secured("ROLE_ADMIN")
    public EmployeeProfileResponse saveEmployee(@RequestBody EmployeeProfileRequest employeeProfileRequest){
        return new EmployeeProfileResponse(employeeProfileService.
                save(new EmployeeProfile(employeeProfileRequest.getName(), employeeProfileRequest.getSurname())));
    }
}
