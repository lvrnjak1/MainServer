package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.business.*;
import ba.unsa.etf.si.mainserver.requests.business.BusinessRequest;
import ba.unsa.etf.si.mainserver.requests.business.EmployeeProfileRequest;
import ba.unsa.etf.si.mainserver.requests.business.OfficeRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.business.BusinessResponse;
import ba.unsa.etf.si.mainserver.responses.business.CashRegisterResponse;
import ba.unsa.etf.si.mainserver.responses.business.EmployeeProfileResponse;
import ba.unsa.etf.si.mainserver.responses.business.OfficeResponse;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.CashRegisterService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
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
    private final OfficeService officeService;
    private final CashRegisterService cashRegisterService;

    public BusinessController(BusinessService businessService, EmployeeProfileService employeeProfileService,
                              OfficeService officeService, CashRegisterService cashRegisterService) {
        this.businessService = businessService;
        this.employeeProfileService = employeeProfileService;
        this.officeService = officeService;
        this.cashRegisterService = cashRegisterService;
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
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            return new BusinessResponse(businessOptional.get());
        }
        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @GetMapping("/{id}/offices")
    @Secured("ROLE_ADMIN")
    public List<OfficeResponse> getAllOfficesForBusiness(@PathVariable("id") Long businessId){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            return businessOptional.get().getOffices().stream().map(OfficeResponse::new).collect(Collectors.toList());
        }
        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @PostMapping("/{id}/restaurant")
    @Secured("ROLE_ADMIN")
    public BusinessResponse toggleRestaurantFeature(@PathVariable("id") Long businessId){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            Boolean presentRestaurantFeature = businessOptional.get().isRestaurantFeature();
            businessOptional.get().setRestaurantFeature(!presentRestaurantFeature);
            return new BusinessResponse(businessService.save(businessOptional.get()));
        }
        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @PostMapping("/{id}/offices")
    @Secured("ROLE_ADMIN")
    public OfficeResponse addOffice(@PathVariable("id") Long businessId,
                                      @RequestBody OfficeRequest officeRequest){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            Business business = businessOptional.get();
            ContactInformation contactInformation = new ContactInformation(officeRequest.getAddress(),
                    officeRequest.getCity(),officeRequest.getCountry(),officeRequest.getEmail(),
                    officeRequest.getPhoneNumber());
            Office office = new Office(contactInformation, business);


            return new OfficeResponse(officeService.save(office));
        }

        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @DeleteMapping("/{businessId}/offices/{officeId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteOffice(@PathVariable("businessId") Long businessId,
                                       @PathVariable("officeId") Long officeId){
        return businessService.findById(businessId).map(business -> officeService.findById(officeId)
                .map(office -> {
            business.getOffices().removeIf(office1 -> office1.getId() == officeId);
            businessService.save(business);
            officeService.delete(office);
            return ResponseEntity.ok(new ApiResponse("Office successfully deleted", 200));
        }).orElseThrow(()->new AppException("Office with id " + officeId + " doesn't exist in that business")))
                .orElseThrow(() -> new AppException("Business with id " + businessId + " doesn't exist"));

    }

    @PostMapping("/{businessId}/offices/{officeId}/cashRegisters") //fali business id
    @Secured("ROLE_ADMIN")
    public CashRegisterResponse addCashRegisterForOffice(@PathVariable("officeId") Long officeId,
                                                         @PathVariable("businessId") Long businessId){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()) {
            Optional<Office> officeOptional = officeService.findByIdInBusiness(officeId, businessOptional.get());
            if (officeOptional.isPresent()) {
                CashRegister cashRegister = new CashRegister(officeOptional.get());
                return new CashRegisterResponse(cashRegisterService.save(cashRegister));
            }
        }
        throw new AppException("Office with id " + officeId + " doesn't exist");

    }

    @DeleteMapping("/{businessId}/offices/{officeId}/cashRegisters/{cashRegId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteCashRegister(@PathVariable("businessId") Long businessId,
                                          @PathVariable("officeId") Long officeId,
                                          @PathVariable("cashRegId") Long cashRegisterId){
        return businessService.findById(businessId).map(business -> officeService.findByIdInBusiness(officeId, business).map(office ->
                    cashRegisterService.findByIdInOffice(cashRegisterId, office).map(cashRegister -> {
                        office.getCashRegisters().removeIf(c -> c.getId().equals(cashRegisterId));
                        officeService.save(office);
                        cashRegisterService.delete(cashRegister);

            return ResponseEntity.ok(new ApiResponse("Cash Register successfully deleted", 200));
        }).orElseThrow(()->new AppException("Cash Register with id " + cashRegisterId + " doesn't exist in that office")))
                .orElseThrow(()->new AppException("Office with id " + officeId + " doesn't exist in that business")))
                    .orElseThrow(() -> new AppException("Business with id " + businessId + " doesn't exist"));

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
