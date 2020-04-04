package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.*;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmploymentHistory;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmploymentHistoryRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.business.*;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.CashServerConfigResponse;
import ba.unsa.etf.si.mainserver.responses.business.*;
import ba.unsa.etf.si.mainserver.responses.transactions.CashRegisterProfitResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.business.BusinessService;
import ba.unsa.etf.si.mainserver.services.business.CashRegisterService;
import ba.unsa.etf.si.mainserver.services.business.EmployeeProfileService;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.transactions.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
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
    private final ReceiptService receiptService;
    private final EmploymentHistoryRepository employmentHistoryRepository;


    public BusinessController(BusinessService businessService, EmployeeProfileService employeeProfileService,
                              OfficeService officeService, CashRegisterService cashRegisterService,
                              CashRegisterRepository cashRegisterRepository,
                              EmployeeProfileRepository employeeProfileRepository,
                              UserService userService, OfficeProfileRepository officeProfileRepository,
                              EmployeeActivityRepository employeeActivityRepository,
                              ReceiptService receiptService,
                              EmploymentHistoryRepository employmentHistoryRepository) {
        this.businessService = businessService;
        this.employeeProfileService = employeeProfileService;
        this.officeService = officeService;
        this.cashRegisterService = cashRegisterService;
        this.cashRegisterRepository = cashRegisterRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.userService = userService;
        this.officeProfileRepository = officeProfileRepository;
        this.employeeActivityRepository = employeeActivityRepository;
        this.receiptService = receiptService;
        this.employmentHistoryRepository = employmentHistoryRepository;
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
                                      @RequestBody OfficeRequest officeRequest) throws ParseException {
        Optional<Business> businessOptional = businessService.findById(businessId);
        if(businessOptional.isPresent()){
            Business business = businessOptional.get();
            ContactInformation contactInformation = new ContactInformation(officeRequest.getAddress(),
                    officeRequest.getCity(),officeRequest.getCountry(),officeRequest.getEmail(),
                    officeRequest.getPhoneNumber());
            Office office = new Office(contactInformation, business, officeRequest.getWorkDayStartDateFromString(),
                    officeRequest.getWorkDayEndDateFromString());
            return new OfficeResponse(officeService.save(office), new ArrayList<>());
        }

        throw new AppException("Business with id " + businessId + " doesn't exist");
    }

    @DeleteMapping("/{businessId}/offices/{officeId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ApiResponse> deleteOffice(@PathVariable("businessId") Long businessId,
                                                    @PathVariable("officeId") Long officeId){
        List<OfficeProfile> officeProfiles = officeProfileRepository.findAllByOfficeIdAndOffice_BusinessId(officeId,businessId);
        if(!officeProfiles.isEmpty()){
            //daj svima otkaz
            for(OfficeProfile officeProfile : officeProfiles){
                List<EmploymentHistory> employmentHistoryList =
                        employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole
                                (officeProfile.getEmployee().getId(), officeProfile.getOffice().getId(),"ROLE_CASHIER");
                List<EmploymentHistory> employmentHistoryList2 =
                        employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole
                                (officeProfile.getEmployee().getId(), officeProfile.getOffice().getId(),"ROLE_BARTENDER");
                List<EmploymentHistory> employmentHistoryList3 =
                        employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole
                                (officeProfile.getEmployee().getId(), officeProfile.getOffice().getId(),"ROLE_OFFICEMAN");
                employmentHistoryList.addAll(employmentHistoryList2);
                employmentHistoryList.addAll(employmentHistoryList3);

                        for(EmploymentHistory employmentHistory: employmentHistoryList){
                            if(employmentHistory.getEndDate() == null){
                                employmentHistory.setEndDate(new Date());
                                employmentHistoryRepository.save(employmentHistory);
                                break;
                            }
                        }
                        officeProfileRepository.delete(officeProfile);
            }
        }
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
            EmployeeProfile employeeProfile = optionalEmployeeProfile.get();
            if(employeeProfile.getAccount().getRoles().stream()
                .noneMatch(role -> role.getName().toString().equals("ROLE_OFFICEMAN")))
            {
                throw new ResourceNotFoundException("This employee isn't officeman");
            }

            Optional<Office> optionalOffice = officeService.findById(officeId);
            if (optionalOffice.isPresent() && optionalOffice.get().getBusiness().getId().equals(businessId)) {
                Office office = optionalOffice.get();
                if(office.getManager() != null &&
                        employeeProfile.getId().equals(office.getManager().getId())){ //ista osoba
                    return ResponseEntity.ok(new ApiResponse(
                            "Employee with id " + optionalEmployeeProfile.get().getId() +
                                    " set as manager for office with id " + office.getId(),200));
                }
                if(office.getManager() != null){ //daje se otkaz starom
                    EmployeeProfile oldManager = office.getManager();

                    Optional<OfficeProfile> officeProfileOptional = officeProfileRepository
                            .findByEmployeeIdAndOfficeId(oldManager.getId(), office.getId());
                    if(!officeProfileOptional.isPresent()){
                        throw new AppException("Error");
                    }
                    OfficeProfile officeProfile = officeProfileOptional.get();
                    officeProfileRepository.delete(officeProfile); //ne radi vise
                    List<EmploymentHistory> employmentHistoryList =
                            employmentHistoryRepository.findAllByEmployeeProfileIdAndOfficeIdAndRole(oldManager.getId(), office.getId(), "ROLE_OFFICEMAN");
                    //mora biti samo jedan
                    for(EmploymentHistory employmentHistory: employmentHistoryList){
                        if(employmentHistory.getEndDate() == null){
                            employmentHistory.setEndDate(new Date());
                            employmentHistoryRepository.save(employmentHistory);
                            break;
                        }
                    }
                }

                Optional<OfficeProfile> optionalOfficeProfile = officeProfileRepository
                        .findByEmployeeIdAndOfficeId(employeeProfile.getId(), office.getId());
                if(!optionalOfficeProfile.isPresent()) { //nije vec zaposlen u ovom officeu
                    officeProfileRepository.save(new OfficeProfile(office, employeeProfile));
                }
                else { //vec zaposlen u ovom ofisu
                    List<EmploymentHistory> employmentHistoryList =
                            employmentHistoryRepository.findAllByEmployeeProfileId(employeeProfile.getId());
                    //mora biti samo jedan
                    for(EmploymentHistory employmentHistory: employmentHistoryList){
                        if(employmentHistory.getEndDate() == null){
                            employmentHistory.setEndDate(new Date());
                            employmentHistoryRepository.save(employmentHistory);
                            break;
                        }
                    }
                }

                EmploymentHistory employmentHistory =
                        new EmploymentHistory(employeeProfile.getId(),office.getId(), new Date(), null, "ROLE_OFFICEMAN");
                employmentHistoryRepository.save(employmentHistory);
                office.setManager(employeeProfile);
                officeService.save(office);
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
    public List<OfficeResponse> getOfficesForEmployee(@PathVariable Long userId,
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

        List<OfficeProfile> officeProfileOptional = officeProfileRepository.findAllByEmployeeId(employeeId);
        if(officeProfileOptional.isEmpty()){
            throw new BadParameterValueException("Employee with this id isn't hired at any office");
        }
        return officeProfileOptional.stream().
                map(
                    officeProfile -> new OfficeResponse(
                            officeProfile.getOffice(),
                            cashRegisterService.getAllCashRegisterResponsesByOfficeId(officeProfile.getOffice().getId()))
                )
                .collect(Collectors.toList());
    }

    @GetMapping("/employees/{userId}/history")
    @Secured("ROLE_MANAGER")
    public EmploymentHistoryResponse getEmployeeHistory(@PathVariable Long userId,
                                                              @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<User> optionalUser = userService.findUserById(userId);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User with id " + userId + " does not exist");
        }
        Optional<EmployeeProfile> employeeProfileOptional = employeeProfileService.findByAccount(optionalUser.get());
        if (!employeeProfileOptional.isPresent()) {
            throw new ResourceNotFoundException("User is not an employee");
        }

        Long employeeId = employeeProfileOptional.get().getId();

        if(!employeeProfileOptional.get().getBusiness().getId().equals(business.getId())){
            throw new BadParameterValueException("Employee with this id doesn't exist");
        }

        List<EmploymentHistory> employmentHistoryList = employmentHistoryRepository.findAllByEmployeeProfileId(employeeId);
        if(employmentHistoryList.isEmpty()){
            throw new BadParameterValueException("Employee doesn't have employment history!");
        }

        List<OfficeHistory> responseList = new ArrayList<>();
        for(EmploymentHistory employmentHistory : employmentHistoryList){
            OfficeResponse officeResponse = null;
            if(employmentHistory.getOfficeId() != null){
                Optional<Office> officeOptional = officeService.findById(employmentHistory.getOfficeId());
                officeResponse = new OfficeResponse(officeOptional.get(),cashRegisterService.getAllCashRegisterResponsesByOfficeId(officeOptional.get().getId()));
            }
            responseList.add(new OfficeHistory(employmentHistory,officeResponse));
        }

        return new EmploymentHistoryResponse(new EmployeeProfileResponse(employeeProfileOptional.get()), responseList);
    }


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

        EmployeeProfile employeeProfile = employeeProfileOptional.get();
        Long employeeId = employeeProfileOptional.get().getId();

        Optional<Office> officeOptional = officeService.findById(hiringRequest.getOfficeId());
        if(!officeOptional.isPresent()){
            throw new ResourceNotFoundException("This office doesn't exist");
        }

        if(!officeOptional.get().getBusiness().getId().equals(business.getId())){
            throw new ResourceNotFoundException("This office doesn't exist");
        }

        //provjeri je li osoba cashier ili bartender
        List<String> roles = employeeProfile.getAccount().getRoles().stream()
                .map(role -> role.getName().toString()).collect(Collectors.toList());
        if(!(roles.contains("ROLE_CASHIER") || roles.contains("ROLE_BARTENDER"))){
            throw new BadParameterValueException("Only cashier and bartender can be hired in office");
        }
        if((!roles.contains("ROLE_CASHIER") && hiringRequest.isCashier()) || (!roles.contains("ROLE_BARTENDER") && !hiringRequest.isCashier())){
            throw new BadParameterValueException("Employees must have appropriate roles for hiring request");
        }

        Optional<OfficeProfile> optionalOfficeProfile = officeProfileRepository.findByEmployeeIdAndOfficeId(employeeId, officeOptional.get().getId());
        if(optionalOfficeProfile.isPresent()){
            throw new BadParameterValueException("Employee is already hired");
        }
        String role = "ROLE_CASHIER";
        if(!hiringRequest.isCashier())
            role = "ROLE_BARTENDER";

        OfficeProfile officeProfile = new OfficeProfile(officeOptional.get(), employeeProfile);
        officeProfileRepository.save(officeProfile);

        EmploymentHistory employmentHistory = new EmploymentHistory(employeeProfile.getId(),officeOptional.get().getId(),
                                                new Date(), null, role);
        employmentHistoryRepository.save(employmentHistory);
        return ResponseEntity.ok(new ApiResponse("Employee successfully hired at this office", 200));
    }


    @DeleteMapping("/employees")
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
        Optional<OfficeProfile> officeProfile = officeProfileRepository.findByEmployeeIdAndOfficeId(employeeProfile.get().getId(), officeOptional.get().getId());
        if(!officeProfile.isPresent()){
            throw new AppException("This office doesn't hire this employee");
        }
        if(officeOptional.get().getManager()!= null && officeOptional.get().getManager().getId().equals(employeeProfile.get().getId())){
            officeOptional.get().setManager(null);
            officeService.save(officeOptional.get());
        }

        officeProfileRepository.delete(officeProfile.get());
        List<EmploymentHistory> employmentHistoryList = employmentHistoryRepository
                .findAllByEmployeeProfileIdAndOfficeId(employeeProfile.get().getId(), officeOptional.get().getId());

        //mora biti samo jedan
        for(EmploymentHistory employmentHistory: employmentHistoryList){
            if(employmentHistory.getEndDate() == null){
                employmentHistory.setEndDate(new Date());
                employmentHistoryRepository.save(employmentHistory);
                return ResponseEntity.ok(new ApiResponse("Employee successfully fired from this office", 200));
            }
        }
        throw new AppException("Error");
    }

    @GetMapping("/offices/{officeId}/cashRegisters")
    @Secured({"ROLE_MERCHANT", "ROLE_MANAGER"})
    public List<CashRegisterProfitResponse> getCashRegistersForOffice(@PathVariable Long officeId,
                                                                @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        Optional<Office> officeOptional = officeService.findById(officeId);
        if(!officeOptional.isPresent()){
            throw new ResourceNotFoundException("Office doesn't exist");
        }

        if(!officeOptional.get().getBusiness().getId().equals(business.getId())){
            throw new UnauthorizedException("Not your office");
        }

        return cashRegisterRepository
                .findAllByOfficeId(officeOptional.get().getId())
                .stream()
                .map(cashRegister -> {
                    BigDecimal dailyProfit = receiptService.findDailyProfitForCashRegister(cashRegister,
                            new Date());
                    BigDecimal totalProfit = receiptService.findTotalProfitForCashRegister(cashRegister);
                    return new CashRegisterProfitResponse(cashRegister.getId(),
                            cashRegister.getName(), dailyProfit, totalProfit);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{businessId}/office-details/{officeId}")
    @Secured("ROLE_OFFICEMAN")
    public CashServerConfigResponse getServerConfig(@PathVariable Long businessId,
                                                    @PathVariable Long officeId,
                                                    @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.getBusinessOfCurrentUser(userPrincipal);
        if(!business.getId().equals(businessId)){
            throw new UnauthorizedException("Not your business");
        }

        Optional<Office> officeOptional = officeService.findById(officeId);
        if(!officeOptional.isPresent()){
            throw new BadParameterValueException("Office doesn't exist");
        }

        if(!officeOptional.get().getBusiness().getId().equals(businessId)){
            throw new BadParameterValueException("This office doesn't belong to this business");
        }

        List<CashRegister> cashRegisters = cashRegisterRepository.findAllByOfficeId(officeId);
        return new CashServerConfigResponse(business.getName(),
                cashRegisters.stream().map(CashRegisterResponse::new).collect(Collectors.toList()));
    }
}
