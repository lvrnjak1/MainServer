package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import ba.unsa.etf.si.mainserver.responses.business.BusinessResponse;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final OfficeRepository officeRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final OfficeService officeService;
    private final UserService userService;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeActivityRepository employeeActivityRepository;

    public BusinessService(BusinessRepository businessRepository, OfficeRepository officeRepository, CashRegisterRepository cashRegisterRepository, OfficeService officeService, UserService userService, EmployeeProfileRepository employeeProfileRepository, EmployeeActivityRepository employeeActivityRepository) {
        this.businessRepository = businessRepository;
        this.officeRepository = officeRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.officeService = officeService;
        this.userService = userService;
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeActivityRepository = employeeActivityRepository;
    }

    public Business save(Business business) {
        return businessRepository.save(business);
    }

    public List<Business> findAll() {
        return businessRepository.findAll();
    }

    public Business findBusinessById(Long businessId){
//        Optional<Business> optionalBusiness = businessRepository.findById(businessId);
//        if (!optionalBusiness.isPresent()) {
//            throw new ResourceNotFoundException("No such business with id " + businessId);
//        }
//        return optionalBusiness.get();
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business with id " + businessId + " doesn't exist"));
    }

    public Business findByName(String businessName) {
        Optional<Business> optionalBusiness = businessRepository.findByName(businessName);
        if (!optionalBusiness.isPresent()) {
            throw new ResourceNotFoundException("No such business with name " + businessName);
        }
        return optionalBusiness.get();
    }

    public Business findBusinessOfCurrentUser(UserPrincipal userPrincipal) {
        User user = userService.findUserByUsername(userPrincipal.getUsername());
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileRepository.findByAccountId(user.getId());
        if (!optionalEmployeeProfile.isPresent()) {
            throw new BadParameterValueException("User is not an employee");
        }
        Optional<EmployeeActivity> employeeActivity = employeeActivityRepository.findByEmployeeProfile(optionalEmployeeProfile.get());
        if(employeeActivity.isPresent()){
            //ova osoba je inactive employee
            throw new ResourceNotFoundException("This employee doesn't exist");
        }
        return findBusinessById(optionalEmployeeProfile.get().getBusiness().getId());
    }

    public List<BusinessResponse> getAllBusinessResponses() {
        List<Business> businesses = businessRepository.findAll();
        return businesses
                .stream()
                .map(
                        business -> new BusinessResponse(
                                business,
                                officeService
                                        .getAllOfficeResponsesByBusinessId(
                                                business.getId()
                                        )
                        )
                ).collect(Collectors.toList());
    }

    public void checkIfTablesAvailable(Business business) {
        if(!business.isRestaurantFeature()){
            throw new AppException("This business is not a restaurant!");
        }
    }

//    public Optional<Business> getBusinessByProductId(Product product){
//        Optional<Office> officeOptional = officeRepository.findById(product.getId());
//        if(officeOptional.isPresent()){
//            return businessRepository.findById(officeOptional.get().getBusiness().getId());
//        }
//
//        throw new AppException("Office with id " + product.getId() + " doesn't exist");
//    }
}
