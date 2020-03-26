package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.requests.BusinessRequest;
import ba.unsa.etf.si.mainserver.requests.CashRegisterRequest;
import ba.unsa.etf.si.mainserver.requests.OfficeRequest;
import ba.unsa.etf.si.mainserver.responses.BusinessResponse;
import ba.unsa.etf.si.mainserver.responses.OfficeResponse;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    //TODO
    //provjeriti koje sve role mogu pristupati kojim rutama

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public BusinessResponse registerNewBusiness(@RequestBody BusinessRequest businessRequest){
        return null;
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public BusinessResponse getBusinessById(@PathVariable("id") Long businessId){
        return null;
    }

    @GetMapping("/{id}/offices")
    @Secured("ROLE_ADMIN")
    public BusinessResponse getAllOfficesForBusiness(@PathVariable("id") Long businessId){
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
    public OfficeResponse addCashRegisterForOffice(@PathVariable("officeId") Long officeId,
                                                   @RequestBody CashRegisterRequest cashRegisterRequest){
        return null;
    }

    @DeleteMapping("/{businessId}/offices/{officeId}/cashRegisters/{cashRegId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteOffice(@PathVariable("businessId") Long businessId,
                                          @PathVariable("officeId") Long officeId,
                                          @PathVariable("cashRegId") Long cashRegisterId){
        return null;
    }


}
