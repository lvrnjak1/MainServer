package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.*;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.business.*;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.business.BusinessResponse;
import ba.unsa.etf.si.mainserver.responses.business.CashRegisterResponse;
import ba.unsa.etf.si.mainserver.responses.business.OfficeResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.CashRegisterService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/business")
@CrossOrigin(origins = "*")
public class BusinessController {

    private final BusinessService businessService;
    private final EmployeeProfileService employeeProfileService;
    private final OfficeService officeService;
    private final CashRegisterService cashRegisterService;
    private final CashRegisterRepository cashRegisterRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final UserService userService;
    private final OfficeProfileRepository officeProfileRepository;
    private final EmployeeActivityRepository employeeActivityRepository;


    public BusinessController(BusinessService businessService, EmployeeProfileService employeeProfileService,
                              OfficeService officeService, CashRegisterService cashRegisterService,
                              CashRegisterRepository cashRegisterRepository,
                              EmployeeProfileRepository employeeProfileRepository,
                              UserService userService, OfficeProfileRepository officeProfileRepository,
                              EmployeeActivityRepository employeeActivityRepository) {
        this.businessService = businessService;
        this.employeeProfileService = employeeProfileService;
        this.officeService = officeService;
        this.cashRegisterService = cashRegisterService;
        this.cashRegisterRepository = cashRegisterRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.userService = userService;
        this.officeProfileRepository = officeProfileRepository;
        this.employeeActivityRepository = employeeActivityRepository;
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public BusinessResponse registerNewBusiness(@RequestBody BusinessRequest businessRequest){
        Optional<EmployeeProfile> employeeProfileOptional = employeeProfileService.findById(businessRequest.getMerchantId());
        if(employeeProfileOptional.isPresent()) {
            Business business = new Business(businessRequest.getName(),
                    businessRequest.isRestaurantFeature(),
                    employeeProfileOptional.get());
            return new BusinessResponse(businessService.save(business),new ArrayList<>());
        }

        throw new AppException("Employee with id " + businessRequest.getMerchantId() + " doesn't exist");
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    public List<BusinessResponse> getAllBusinesses(){
        return businessService.getAllBusinessResponses();
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public BusinessResponse getBusinessById(@PathVariable("id") Long businessId){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            return new BusinessResponse(
                    businessOptional.get(),
                    officeService.getAllOfficeResponsesByBusinessId(businessId));
        }
        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @GetMapping("/{id}/offices")
    @Secured({"ROLE_ADMIN","ROLE_MERCHANT","ROLE_MANAGER"})
    public List<OfficeResponse> getAllOfficesForBusiness(@PathVariable("id") Long businessId){
        Optional<Business> optionalBusiness = businessService.findById(businessId);
        if (!optionalBusiness.isPresent()) {
            throw new ResourceNotFoundException("Business with id " + businessId + " not found");
        }
        return officeService
                .findByBusiness(
                        optionalBusiness.get()
                )
                .stream()
                .map(
                        office -> new OfficeResponse(
                                office,
                                cashRegisterService.getAllCashRegisterResponsesByOfficeId(office.getId()))
                )
                .collect(Collectors.toList());
    }

    @GetMapping("/offices")
    @Secured({"ROLE_MERCHANT","ROLE_MANAGER", "ROLE_WAREMAN", "ROLE_PRW"})
    public List<OfficeResponse> getAllOfficesForMyBusiness(@CurrentUser UserPrincipal userPrincipal) {
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        return officeService
                .findByBusiness(
                        business
                )
                .stream()
                .map(
                        office -> new OfficeResponse(
                                office,
                                cashRegisterService.getAllCashRegisterResponsesByOfficeId(office.getId()))
                )
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/restaurant")
    @Secured("ROLE_ADMIN")
    public BusinessResponse toggleRestaurantFeature(@PathVariable("id") Long businessId){
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            Boolean presentRestaurantFeature = businessOptional.get().isRestaurantFeature();
            businessOptional.get().setRestaurantFeature(!presentRestaurantFeature);
            return new BusinessResponse(
                    businessService.save(businessOptional.get()),
                    officeService.getAllOfficeResponsesByBusinessId(businessId));
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
            return new OfficeResponse(officeService.save(office), new ArrayList<>());
        }

        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @DeleteMapping("/{businessId}/offices/{officeId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ApiResponse> deleteOffice(@PathVariable("businessId") Long businessId,
                                                    @PathVariable("officeId") Long officeId){
        return ResponseEntity.ok(officeService.deleteOfficeOfBusiness(officeId, businessId));
    }

    // TODO make update route(/{businessId}/offices/{officeId}) for admin

    @PostMapping("/{businessId}/offices/{officeId}/cashRegisters")
    @Secured("ROLE_ADMIN")
    public CashRegisterResponse addCashRegisterForOffice(@PathVariable("officeId") Long officeId,
                                                                         @PathVariable("businessId") Long businessId,
                                                                         @RequestBody CashRegisterRequest cashRegisterRequest){
        return new CashRegisterResponse(cashRegisterService
                .createCashRegisterInOfficeOfBusiness(officeId,businessId, cashRegisterRequest.getName()));
    }

    @DeleteMapping("/{businessId}/offices/{officeId}/cashRegisters/{cashRegId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteCashRegister(@PathVariable("businessId") Long businessId,
                                          @PathVariable("officeId") Long officeId,
                                          @PathVariable("cashRegId") Long cashRegisterId){
        return ResponseEntity.ok(businessService.deleteCashRegisterFromOfficeOfBusiness(cashRegisterId, officeId, businessId));
    }


    @PostMapping("/{businessId}/offices/{officeId}/manager")
    @Secured({"ROLE_ADMIN", "ROLE_MERCHANT", "ROLE_MANAGER"})
    public ResponseEntity<ApiResponse> setManager(@RequestBody OfficeManagerRequest officeManagerRequest,
                                                  @PathVariable Long businessId, @PathVariable Long officeId) {
        Optional<EmployeeProfile> optionalEmployeeProfile = Optional.empty();
        if (officeManagerRequest.getUserId() != null) {
            optionalEmployeeProfile = employeeProfileRepository
                    .findByContactInformation_EmailOrAccount_UsernameOrAccount_IdOrId(
                            "",
                            "",
                            officeManagerRequest.getUserId(),
                            -1L);
        } else if (officeManagerRequest.getEmail() != null) {
            optionalEmployeeProfile = employeeProfileRepository
                    .findByContactInformation_EmailOrAccount_UsernameOrAccount_IdOrId(
                            officeManagerRequest.getEmail(),
                            "",
                            -1L,
                            -1L);
        } else if (officeManagerRequest.getUsername() != null) {
            optionalEmployeeProfile = employeeProfileRepository
                    .findByContactInformation_EmailOrAccount_UsernameOrAccount_IdOrId(
                            "",
                            officeManagerRequest.getUsername(),
                            -1L,
                            -1L);
        } else if (officeManagerRequest.getEmployeeId() != null) {
            optionalEmployeeProfile = employeeProfileRepository
                    .findByContactInformation_EmailOrAccount_UsernameOrAccount_IdOrId(
                            "",
                            "",
                            -1L,
                            officeManagerRequest.getEmployeeId());
        }

        if (optionalEmployeeProfile.isPresent()) {
            Optional<EmployeeActivity> employeeActivity = employeeActivityRepository.findByEmployeeProfile(optionalEmployeeProfile.get());
            if(employeeActivity.isPresent()){
                //ova osoba je inactive employee
                throw new ResourceNotFoundException("This employee doesn't exist");
            }
            Optional<Office> optionalOffice = officeService.findById(officeId);
            if (optionalOffice.isPresent() && optionalOffice.get().getBusiness().getId().equals(businessId)) {
                Office office = optionalOffice.get();
                office.setManager(optionalEmployeeProfile.get());
                officeService.save(office);
                User user = userService.changeUserRoles(optionalEmployeeProfile.get().getAccount().getId(),
                        new ArrayList<>(Collections.singletonList("ROLE_OFFICEMAN")));
                System.out.println(user.getRoles());
                officeProfileRepository.save(new OfficeProfile(null, office, optionalEmployeeProfile.get()));
                return ResponseEntity.ok(new ApiResponse(
                        "Employee with id " +
                                optionalEmployeeProfile.get().getId() +
                                " set as manager for office with id " +
                                office.getId(),200));
            }
        }
        throw new AppException("Bad request");
    }

    @GetMapping("/employees/{userId}/office")
    @Secured("ROLE_MANAGER")
    public OfficeResponse getOfficeForEmployee(@PathVariable Long userId,
                                               @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<EmployeeProfile> employeeProfileOptional = employeeProfileRepository.findByAccount_Id(userId);
        if(!employeeProfileOptional.isPresent()){
            throw new BadParameterValueException("Employee with this id doesn't exist");
        }

        Long employeeId = employeeProfileOptional.get().getId();

        if(!employeeProfileOptional.get().getBusiness().getId().equals(business.getId())){
            throw new BadParameterValueException("Employee with this id doesn't exist");
        }

        Optional<OfficeProfile> officeProfileOptional = officeProfileRepository.findByEmployee_Id(employeeId);
        if(!officeProfileOptional.isPresent()){
            throw new BadParameterValueException("Employee with this id isn't hired at any office");
        }

        Office office = officeProfileOptional.get().getOffice();
        return new OfficeResponse(office, cashRegisterService.getAllCashRegisterResponsesByOfficeId(office.getId()));
    }

//TODO fire employees
//id-evi ne valjaju omg
    @PostMapping("/employees")
    @Secured("ROLE_MANAGER")
    public ResponseEntity<ApiResponse> hireEmployeeForOffice(@CurrentUser UserPrincipal userPrincipal,
                                                             @RequestBody HiringRequest hiringRequest){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);

        Optional<EmployeeProfile> employeeProfileOptional = employeeProfileRepository
                .findByAccount_Id(hiringRequest.getEmployeeId());
        if(!employeeProfileOptional.isPresent()){
            throw new BadParameterValueException("Employee with this id doesn't exist");
        }

        Long employeeId = employeeProfileOptional.get().getId();

        Optional<Office> officeOptional = officeService.findById(hiringRequest.getOfficeId());
        if(!officeOptional.isPresent()){
            throw new ResourceNotFoundException("This office doesn't exist");
        }

        if(!officeOptional.get().getBusiness().getId().equals(business.getId())){
            throw new ResourceNotFoundException("This office doesn't exist");
        }

        Optional<EmployeeProfile> employeeProfile  = employeeProfileService.findById(employeeId);
        if(!employeeProfile.isPresent()){
            throw new ResourceNotFoundException("This employee doesn't exist");
        }

        Optional<OfficeProfile> officeProfileOptional = officeProfileRepository
                .findByEmployee_Id(employeeProfile.get().getId());

        if(officeProfileOptional.isPresent()){
            throw new BadParameterValueException("This employee is already hired at this office");
        }

        OfficeProfile officeProfile = new OfficeProfile(officeOptional.get(), employeeProfile.get());
        officeProfileRepository.save(officeProfile);
        return ResponseEntity.ok(new ApiResponse("Employee successfully hired at this office", 200));
    }


    @DeleteMapping("/employees") //upitno
    @Secured("ROLE_MANAGER")
    public ResponseEntity<ApiResponse> fireEmployeeFromOffice(@CurrentUser UserPrincipal userPrincipal,
                                                             @RequestBody HiringRequest hiringRequest){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Office> officeOptional = officeService.findById(hiringRequest.getOfficeId());
        if(!officeOptional.isPresent()){
            throw new ResourceNotFoundException("This office doesn't exist");
        }

        Optional<EmployeeProfile> employeeProfile  = employeeProfileService.findById(hiringRequest.getEmployeeId());
        if(!employeeProfile.isPresent()){
            throw new ResourceNotFoundException("This employee doesn't exist");
        }

        Optional<OfficeProfile> officeProfile = officeProfileRepository.findByEmployee_Id(employeeProfile.get().getId());
        if(!officeProfile.isPresent() || !officeProfile.get().getOffice().getId().equals(officeOptional.get().getId())){
            throw new AppException("This office doesn't hire this employee");
        }
        if(officeOptional.get().getManager().getId().equals(employeeProfile.get().getId())){
            officeOptional.get().setManager(null);
            officeService.save(officeOptional.get());
        }

        officeProfileRepository.delete(officeProfile.get());
        return ResponseEntity.ok(new ApiResponse("Employee successfully fired from this office", 200));
    }
}
