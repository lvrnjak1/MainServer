package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.configurations.Actions;
import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.Language;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.*;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import ba.unsa.etf.si.mainserver.requests.business.*;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationPayload;
import ba.unsa.etf.si.mainserver.requests.notifications.NotificationRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.CashServerConfigResponse;
import ba.unsa.etf.si.mainserver.responses.business.*;
import ba.unsa.etf.si.mainserver.responses.pr.OfficeResponseLite;
import ba.unsa.etf.si.mainserver.responses.transactions.CashRegisterProfitResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
import ba.unsa.etf.si.mainserver.services.business.*;
import ba.unsa.etf.si.mainserver.services.transactions.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final LogServerService logServerService;

    public BusinessController(BusinessService businessService, EmployeeProfileService employeeProfileService,
                              OfficeService officeService, CashRegisterService cashRegisterService,
                              CashRegisterRepository cashRegisterRepository,
                              EmployeeProfileRepository employeeProfileRepository,
                              UserService userService, OfficeProfileRepository officeProfileRepository,
                              EmployeeActivityRepository employeeActivityRepository,
                              ReceiptService receiptService, LogServerService logServerService) {
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
        this.logServerService = logServerService;
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public BusinessResponse registerNewBusiness(
            @RequestBody BusinessRequest businessRequest,
            @CurrentUser UserPrincipal userPrincipal){
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeById(businessRequest.getMerchantId());
        User user = userService.findUserById(employeeProfile.getAccount().getId());
        Business business = new Business(businessRequest.getName(),
                businessRequest.isRestaurantFeature(),
                employeeProfile);
        businessService.save(business);
        employeeProfile.setBusiness(business);
        employeeProfileService.save(employeeProfile);
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.ADMIN_CREATE_BUSINESS_ACTION_NAME,
                "business",
                "Admin " + userPrincipal.getUsername() + " has a business " + business.getName()
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return new BusinessResponse(business,new ArrayList<>());
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    public List<BusinessResponse> getAllBusinesses(){
        return businessService.getAllBusinessResponses();
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public BusinessResponse getBusinessById(@PathVariable("id") Long businessId){
        Business business = businessService.findBusinessById(businessId);
            return new BusinessResponse(
                    business,
                    officeService.getAllOfficeResponsesByBusinessId(businessId));
    }

    @GetMapping("/{id}/offices")
    @Secured({"ROLE_ADMIN","ROLE_MERCHANT","ROLE_MANAGER"})
    public List<OfficeResponse> getAllOfficesForBusiness(@PathVariable("id") Long businessId){
        Business business = businessService.findBusinessById(businessId);
        return officeService
                .findAllByBusiness(business)
                .stream()
                .map(
                        office -> new OfficeResponse(
                                office,
                                cashRegisterService.getAllCashRegisterResponsesByOfficeId(office.getId()))
                )
                .collect(Collectors.toList());
    }

    @GetMapping("/offices")
    @Secured({"ROLE_MERCHANT","ROLE_MANAGER", "ROLE_WAREMAN", "ROLE_PRW", "ROLE_PRP"})
    public List<OfficeResponse> getAllOfficesForMyBusiness(@CurrentUser UserPrincipal userPrincipal) {
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return officeService
                .findAllByBusiness(
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
    public BusinessResponse toggleRestaurantFeature(
            @PathVariable("id") Long businessId,
            @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessById(businessId);
        boolean presentRestaurantFeature = business.isRestaurantFeature();
        business.setRestaurantFeature(!presentRestaurantFeature);
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.ADMIN_TOGGLE_RESTAURANT_ACTION_NAME,
                "restaurant",
                "Admin " + userPrincipal.getUsername() + " has toggled the restaurant feature to " +
                        business.isRestaurantFeature() + "!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return new BusinessResponse(
                businessService.save(business),
                officeService.getAllOfficeResponsesByBusinessId(businessId));
    }

    @PostMapping("/{id}/offices")
    @Secured("ROLE_ADMIN")
    public OfficeResponse addOffice(
            @PathVariable("id") Long businessId,
            @RequestBody OfficeRequest officeRequest,
            @CurrentUser UserPrincipal userPrincipal) throws ParseException {

        Business business = businessService.findBusinessById(businessId);
        if(business.getMaxNumberOffices() == businessService.countOfficesInBusiness(businessId)){
            throw new AppException("Business has reached max number of offices");
        }
        ContactInformation contactInformation = new ContactInformation(officeRequest.getAddress(),
                officeRequest.getCity(), officeRequest.getCountry(), officeRequest.getEmail(),
                officeRequest.getPhoneNumber());
        Office office = new Office(contactInformation, business, officeRequest.getWorkDayStartDateFromString(),
                officeRequest.getWorkDayEndDateFromString());

        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.ADMIN_CREATE_OFFICE_ACTION_NAME,
                "office",
                "Admin " + userPrincipal.getUsername() + " has created an office!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "office",
                                "open_office",
                                "Office in " +
                                        office.getContactInformation().getCity() +
                                        " " +
                                        office.getContactInformation().getAddress() +
                                        " has been opened."
                        )
                ),
                "user_management"
        );
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "office",
                                "open_office",
                                "Office in " +
                                        office.getContactInformation().getCity() +
                                        " " +
                                        office.getContactInformation().getAddress() +
                                        " has been opened."
                        )
                ),
                "warehouse"
        );
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "office",
                                "open_office",
                                "Office in " +
                                        office.getContactInformation().getCity() +
                                        " " +
                                        office.getContactInformation().getAddress() +
                                        " has been opened."
                        )
                ),
                "merchant_dashboard"
        );
        Office savedOffice = officeService.save(office);
        businessService.createServer(business, office,
                officeRequest.getServerUsername(), officeRequest.getServerPassword());
        return new OfficeResponse(savedOffice, new ArrayList<>());
    }

    @DeleteMapping("/{businessId}/offices/{officeId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ApiResponse> deleteOffice(
            @PathVariable("businessId") Long businessId,
            @PathVariable("officeId") Long officeId,
            @CurrentUser UserPrincipal userPrincipal) {

        Office office = officeService.findOfficeById(officeId, businessId);
        office.setManager(null);

        employeeProfileService.deleteAllEmployeesFromOffice(office);

        if (office.getBusiness().getMainOfficeId() != null &&
                office.getBusiness().getMainOfficeId().equals(officeId)) {
            office.getBusiness().setMainOfficeId(null);
            businessService.save(office.getBusiness());
        }
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.ADMIN_DELETE_OFFICE_ACTION_NAME,
                "office",
                "Admin " + userPrincipal.getUsername() + " has deleted an office!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "office",
                                "close_office",
                                "Office in " +
                                        office.getContactInformation().getCity() +
                                        " " +
                                        office.getContactInformation().getAddress() +
                                        " has been closed."
                        )
                ),
                "warehouse"
        );
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "office",
                                "close_office",
                                "Office in " +
                                        office.getContactInformation().getCity() +
                                        " " +
                                        office.getContactInformation().getAddress() +
                                        " has been closed."
                        )
                ),
                "merchant_dashboard"
        );
        return ResponseEntity.ok(officeService.deleteOfficeOfBusiness(officeId, businessId));
    }



    // TODO make update route(/{businessId}/offices/{officeId}) for admin

    @PutMapping("/{businessId}/offices/{officeId}/maxCashRegisters")
    @Secured("ROLE_ADMIN")
    public ApiResponse ChangeMaxNumberCashRegisters(
            @PathVariable("officeId") Long officeId,
            @PathVariable("businessId") Long businessId,
            @RequestBody MaxRequest maxRequest) {

        Office office = officeService.findOfficeById(officeId, businessId);
        if(officeService.countCashRegsitersInOffice(officeId) > maxRequest.getMax()){
            throw new AppException("Currently there are more cash registers in office than " + maxRequest.getMax());
        }
        office.setMaxNumberCashRegisters(maxRequest.getMax());
        officeService.save(office);

        return new ApiResponse("Max number of cash registers in office changed to " + maxRequest.getMax(),
                200);
    }

    @PostMapping("/{businessId}/offices/{officeId}/cashRegisters")
    @Secured("ROLE_ADMIN")
    public CashRegisterWithUUIDResponse addCashRegisterForOffice(
            @PathVariable("officeId") Long officeId,
            @PathVariable("businessId") Long businessId,
            @RequestBody CashRegisterRequest cashRegisterRequest,
            @CurrentUser UserPrincipal userPrincipal) {
        Office office = officeService.findOfficeById(officeId, businessId);
        if(office.getMaxNumberCashRegisters() == officeService.countCashRegsitersInOffice(officeId)){
            throw new AppException("Office has reached max number of cash registers");
        }
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.ADMIN_CREATE_CASH_REGISTER_ACTION_NAME,
                "cash_register",
                "Admin " + userPrincipal.getUsername() + " has created a cash register!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return new CashRegisterWithUUIDResponse(cashRegisterService
                .createCashRegisterInOfficeOfBusiness(officeId, businessId, cashRegisterRequest.getName(), cashRegisterRequest.getUuid()));
    }

    @GetMapping("/{businessId}/offices/{officeId}/cashRegisters/{cashRegisterId}")
    @Secured("ROLE_ADMIN")
    public CashRegisterWithUUIDResponse getCashRegister(@PathVariable("officeId") Long officeId,
                                                        @PathVariable("businessId") Long businessId,
                                                        @PathVariable("cashRegisterId") Long cashRegisterId){
        return new CashRegisterWithUUIDResponse(cashRegisterService.findCashRegisterById(cashRegisterId, officeId, businessId));
    }

    @PostMapping("/{businessId}/offices/{officeId}/cashRegisters/{cashRegisterId}")
    @Secured("ROLE_ADMIN")
    public CashRegisterWithUUIDResponse editCashRegister(
            @PathVariable("officeId") Long officeId,
            @PathVariable("businessId") Long businessId,
            @PathVariable("cashRegisterId") Long cashRegisterId,
            @RequestBody CashRegisterRequest cashRegisterRequest,
            @CurrentUser UserPrincipal userPrincipal) {

        CashRegister cashRegister = cashRegisterService.findCashRegisterById(cashRegisterId, officeId, businessId);
        if (cashRegisterRequest.getUuid() != null)
            cashRegister.setUuid(cashRegisterRequest.getUuid());
        if (cashRegisterRequest.getName() != null)
            cashRegister.setName(cashRegisterRequest.getName());
        cashRegisterService.save(cashRegister);
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.ADMIN_EDIT_CASH_REGISTER_ACTION_NAME,
                "cash_register",
                "Admin " + userPrincipal.getUsername() + " has edit a cash register!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return new CashRegisterWithUUIDResponse(cashRegister);
    }

    @DeleteMapping("/{businessId}/offices/{officeId}/cashRegisters/{cashRegId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteCashRegister(
            @PathVariable("businessId") Long businessId,
            @PathVariable("officeId") Long officeId,
            @PathVariable("cashRegId") Long cashRegisterId,
            @CurrentUser UserPrincipal userPrincipal) {
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.ADMIN_DELETE_CASH_REGISTER_ACTION_NAME,
                "cash_register",
                "Admin " + userPrincipal.getUsername() + " has deleted a cash register!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return ResponseEntity.ok(cashRegisterService.deleteCashRegisterByIdFromOfficeOfBusiness(cashRegisterId, officeId, businessId));
    }


    @PostMapping("/{businessId}/offices/{officeId}/manager")
    @Secured({"ROLE_ADMIN", "ROLE_MERCHANT", "ROLE_MANAGER"})
    public ResponseEntity<ApiResponse> setManager(
            @RequestBody OfficeManagerRequest officeManagerRequest,
            @PathVariable Long businessId, @PathVariable Long officeId,
            @CurrentUser UserPrincipal userPrincipal) {

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
            if (employeeActivity.isPresent()) {
                //ova osoba je inactive employee
                throw new ResourceNotFoundException("This employee doesn't exist");
            }
            EmployeeProfile employeeProfile = optionalEmployeeProfile.get();
            if (employeeProfile.getAccount().getRoles().stream()
                    .noneMatch(role -> role.getName().toString().equals("ROLE_OFFICEMAN"))) {
                throw new ResourceNotFoundException("This employee isn't officeman");
            }

            Office office = officeService.findOfficeById(officeId, businessId);

            if (office.getManager() != null &&
                    employeeProfile.getId().equals(office.getManager().getId())) { //ista osoba
                return ResponseEntity.ok(new ApiResponse(
                        "Employee with id " + employeeProfile.getId() +
                                " set as manager for office with id " + office.getId(), 200));
            }
            if (office.getManager() != null) { //daje se otkaz starom
                EmployeeProfile oldManager = office.getManager();
                employeeProfileService.unassignEmployeeFromOffice(oldManager, office);
            }
            employeeProfileService.assignEmployeeToOffice(employeeProfile, office, "ROLE_OFFICEMAN");
            office.setManager(employeeProfile);
            officeService.save(office);
            // DO NOT EDIT THIS CODE BELOW, EVER
            logServerService.documentAction(
                    userPrincipal.getUsername(),
                    Actions.ADMIN_CREATE_CASH_REGISTER_ACTION_NAME,
                    "cash_register",
                    "User " + userPrincipal.getUsername() + " has set manager" + employeeProfile.getName()
            );
            // DO NOT EDIT THIS CODE ABOVE, EVER
            return ResponseEntity.ok(new ApiResponse(
                    "Employee with id " + employeeProfile.getId() +
                            " set as manager for office with id " + office.getId(), 200));
        }
        throw new AppException("Bad request");
    }
    @GetMapping("/employees/{userId}/office")
    @Secured("ROLE_MANAGER")
    public List<OfficeResponse> getOfficesForEmployee(@PathVariable Long userId,
                                               @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        User user = userService.findUserById(userId);
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);

        if(!employeeProfile.getBusiness().getId().equals(business.getId())){
            throw new BadParameterValueException("Employee with this id doesn't exist");
        }

        List<OfficeProfile> officeProfileOptional = officeProfileRepository.findAllByEmployeeId(employeeProfile.getId());
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




    @PostMapping("/employees")
    @Secured("ROLE_MANAGER")
    public ResponseEntity<ApiResponse> assignEmployeeForOffice(@CurrentUser UserPrincipal userPrincipal,
                                                               @RequestBody HiringRequest hiringRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        User user = userService.findUserById(hiringRequest.getEmployeeId());
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);
        Office office = officeService.findOfficeById(hiringRequest.getOfficeId(), business.getId());

        //provjeri je li osoba cashier ili bartender
        List<String> roles = employeeProfile.getAccount().getRoles().stream()
                .map(role -> role.getName().toString()).collect(Collectors.toList());
        if(!(roles.contains("ROLE_CASHIER") || roles.contains("ROLE_BARTENDER"))){
            throw new BadParameterValueException("Only cashier and bartender can be hired in office");
        }
        if((!roles.contains("ROLE_CASHIER") && hiringRequest.isCashier()) || (!roles.contains("ROLE_BARTENDER") && !hiringRequest.isCashier())){
            throw new BadParameterValueException("Employees must have appropriate roles for hiring request");
        }
        String role = "ROLE_CASHIER";
        if(!hiringRequest.isCashier()){
            role = "ROLE_BARTENDER";
        }

        employeeProfileService.assignEmployeeToOffice(employeeProfile, office, role);
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.ASSIGN_EMPLOYEE_FOR_OFFICE_ACTION_NAME,
                "employee",
                "Manager " + userPrincipal.getUsername() + " has assigned employee to office"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "info",
                        new NotificationPayload(
                                "employee",
                                "assign_employee",
                                employeeProfile.getName() + " " + employeeProfile.getSurname() + " has been assigned to office!"
                        )
                )
                ,
                "merchant_dashboard"
        );
        return ResponseEntity.ok(new ApiResponse("Employee successfully assigned to this office", 200));
    }


    @DeleteMapping("/employees")
    @Secured("ROLE_MANAGER")
    public ResponseEntity<ApiResponse> unassignEmployeeFromOffice(@CurrentUser UserPrincipal userPrincipal,
                                                                  @RequestBody HiringRequest hiringRequest){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        User user = userService.findUserById(hiringRequest.getEmployeeId());
        EmployeeProfile employeeProfile = employeeProfileService.findEmployeeByAccount(user);
        Office office = officeService.findOfficeById(hiringRequest.getOfficeId(), business.getId());

        employeeProfileService.unassignEmployeeFromOffice(employeeProfile, office);

        if(office.getManager()!= null && office.getManager().getId().equals(employeeProfile.getId())){
            office.setManager(null);
            officeService.save(office);
        }
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.UNASSIGN_EMPLOYEE_FOR_OFFICE_ACTION_NAME,
                "employee",
                "Employee " + userPrincipal.getUsername() + " has unassigned employee from office!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        logServerService.broadcastNotification(
                new NotificationRequest(
                        "warning",
                        new NotificationPayload(
                                "employee",
                                "unassign_employee",
                                employeeProfile.getName() + " " + employeeProfile.getSurname() + " has been unassigned from office!"
                        )
                )
                ,
                "merchant_dashboard"
        );
        return ResponseEntity.ok(new ApiResponse("Employee successfully unassigned from this office", 200));

    }

    @GetMapping("/offices/{officeId}/cashRegisters")
    @Secured({"ROLE_MERCHANT", "ROLE_MANAGER"})
    public List<CashRegisterProfitResponse> getCashRegistersForOffice(@PathVariable Long officeId,
                                                                @CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Office office = officeService.findOfficeById(officeId, business.getId());
        return getCashRegisterProfitResponses(office);
    }

    @GetMapping("/offices/{officeId}/cashRegisters/profit")
    @Secured("ROLE_ADMIN")
    public List<CashRegisterProfitResponse> getProfitForAllCashRegisters(@PathVariable Long officeId){

        Optional<Office> officeOptional = officeService.findById(officeId);
        if(!officeOptional.isPresent()){
            throw new ResourceNotFoundException("Office doesn't exist");
        }

        return getCashRegisterProfitResponses(officeOptional.get());
    }

    private List<CashRegisterProfitResponse> getCashRegisterProfitResponses(Office office) {
        return cashRegisterRepository
                .findAllByOfficeId(office.getId())
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

    @GetMapping("office-details")
    @Secured("ROLE_OFFICEMAN")
    public CashServerConfigResponse getServerConfig(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Office office = officeService.findByManager(userPrincipal);

        List<CashRegister> cashRegisters = cashRegisterRepository.findAllByOfficeId(office.getId());
        return new CashServerConfigResponse(business.getName(),business.isRestaurantFeature(),
                cashRegisters.stream().map(CashRegisterWithUUIDResponse::new).collect(Collectors.toList()),
                office.getLanguageName().toString(),
                business.getStringSyncDate(),
                office.getStringStart(),
                office.getStringEnd(),
                business.getPlaceName(),
                business.getId(),
                office.getId());
    }

    //ruta za PR app da vide informacije o svim offices u svim businesses
    @GetMapping("/allOffices")
    public List<OfficeResponseLite> getAllOfficesForAllBusinesses(){
        return officeService.findAll()
                .stream()
                .map(OfficeResponseLite::new)
                .collect(Collectors.toList());
    }

    //ruta za PR app da vide informacije o svim biznisima
    @GetMapping("/allBusinesses")
    public List<BusinessResponse> getAllBusinessesForPR(){
        return businessService.getAllBusinessResponses();
    }

    @PutMapping("/mainOffice")
    @Secured("ROLE_MERCHANT")
    public ApiResponse setMainOffice(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody MainOfficeRequest mainOfficeRequest) {

        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        Optional<Office> officeOptional = officeService.findById(mainOfficeRequest.getMainOfficeId());
        if (!officeOptional.isPresent()) {
            throw new ResourceNotFoundException("Office doesn't exist");
        }

        if (!officeOptional.get().getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your office");
        }

        business.setMainOfficeId(mainOfficeRequest.getMainOfficeId());
        businessService.save(business);
        // DO NOT EDIT THIS CODE BELOW, EVER
        logServerService.documentAction(
                userPrincipal.getUsername(),
                Actions.SET_MAIN_OFFICE_ACTION_NAME,
                "office",
                "Merchant " + userPrincipal.getUsername() + " has set main office!"
        );
        // DO NOT EDIT THIS CODE ABOVE, EVER
        return new ApiResponse("Office with id " + mainOfficeRequest.getMainOfficeId()
                + " set as main office of your business", 200);
    }

    @PutMapping("/{businessId}/maxOffices")
    @Secured("ROLE_ADMIN")
    public ApiResponse changeMaxNumberOffices(
            @PathVariable("businessId") Long businessId,
            @RequestBody MaxRequest maxRequest) {

        Business business = businessService.findBusinessById(businessId);
        if(businessService.countOfficesInBusiness(businessId) > maxRequest.getMax()){
            throw new AppException("Currently there are more offices in business than " + maxRequest.getMax());
        }
        business.setMaxNumberOffices(maxRequest.getMax());
        businessService.save(business);

        return new ApiResponse("Max number of offices in business changed to " + maxRequest.getMax(),
                200);
    }

    @GetMapping("/mainOffice")
    @Secured({"ROLE_MERCHANT", "ROLE_PRW", "ROLE_PRP", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_WAREMAN", "ROLE_OFFICEMAN"})
    public MainOfficeResponse getMyMainOffice(@CurrentUser UserPrincipal userPrincipal){
        Business business = businessService.findBusinessOfCurrentUser(userPrincipal);
        return new MainOfficeResponse(business.getMainOfficeId());
    }

    //ruta za dzavidove korisnike
    @GetMapping("/{businessId}/mainOffice")
    public MainOfficeResponse getMainOfficeForBusiness(@PathVariable Long businessId){
        Business business = businessService.findBusinessById(businessId);
        return new MainOfficeResponse(business.getMainOfficeId());
    }

    @GetMapping("/languages")
    public List<LanguageResponse> getAllLanguages(){
        return Stream.of(Language.values())
                .map(language -> new LanguageResponse(language.toString()))
                .collect(Collectors.toList());
    }

    @PutMapping("/{businessId}/offices/{officeId}/language")
    @Secured("ROLE_ADMIN")
    public ApiResponse ChangeOfficeDefaultLanguage(
            @PathVariable("officeId") Long officeId,
            @PathVariable("businessId") Long businessId,
            @RequestBody LanguageRequest languageRequest) {

        Office office = officeService.findOfficeById(officeId, businessId);
        try {
            office.setLanguage(languageRequest.getLanguage());
            officeService.save(office);
        }
        catch (IllegalArgumentException e){
            throw new BadParameterValueException("Language is not defined");
        }

        return new ApiResponse("Office language set to " + languageRequest.getLanguage(),
                200);
    }

    @PutMapping("/{businessId}/syncTime")
    @Secured("ROLE_ADMIN")
    public ApiResponse ChangeSyncTime(
            @PathVariable("businessId") Long businessId,
            @RequestBody SyncTimeRequest syncTimeRequest) throws ParseException {

        Business business = businessService.findBusinessById(businessId);
        business.setSyncTime(syncTimeRequest.getSyncTimeFromString());
        businessService.save(business);

        return new ApiResponse("Synchronization time set to " + syncTimeRequest.getSyncTime(),
                200);
    }

    @PutMapping("/{businessId}/offices/{officeId}/workHours")
    @Secured("ROLE_ADMIN")
    public ApiResponse ChangeOfficeWorkHours(
            @PathVariable("businessId") Long businessId,
            @PathVariable("officeId") Long officeId,
            @RequestBody WorkHoursRequest workHoursRequest) throws ParseException {

        Office office = officeService.findOfficeById(officeId,businessId);
        office.setWorkDayStart(workHoursRequest.getStartTimeFromString());
        office.setWorkDayEnd(workHoursRequest.getEndTimeFromString());
        officeService.save(office);
        return new ApiResponse("Office work hours successfully changed",
                200);
    }

    @PutMapping("/{businessId}/placeName")
    @Secured("ROLE_ADMIN")
    public ApiResponse ChangePlaceName(
            @PathVariable("businessId") Long businessId,
            @RequestBody PlaceNameRequest placeNameRequest){

        Business business = businessService.findBusinessById(businessId);
        business.setPlaceName(placeNameRequest.getPlaceName());
        businessService.save(business);

        return new ApiResponse("Business place name changed!", 200);
    }
}
