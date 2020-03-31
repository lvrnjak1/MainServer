package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
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

    public BusinessService(BusinessRepository businessRepository, OfficeRepository officeRepository, CashRegisterRepository cashRegisterRepository, OfficeService officeService, UserService userService, EmployeeProfileRepository employeeProfileRepository) {
        this.businessRepository = businessRepository;
        this.officeRepository = officeRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.officeService = officeService;
        this.userService = userService;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    public Business save(Business business) {
        return businessRepository.save(business);
    }

    public List<Business> findAll() {
        return businessRepository.findAll();
    }

    public Optional<Business> findById(Long businessId){
        return businessRepository.findById(businessId);
    }

    public ApiResponse deleteCashRegisterFromOfficeOfBusiness(Long cashRegisterId, Long officeId, Long businessId) {
        Optional<Business> optionalBusiness = businessRepository.findById(businessId);
        if (!optionalBusiness.isPresent()) {
            throw new ResourceNotFoundException("No such business with id " + businessId);
        }
        Optional<Office> optionalOffice = officeRepository.findById(officeId);
        if (!optionalOffice.isPresent()) {
            throw new ResourceNotFoundException("No such office with id " + officeId);
        }
        if (!optionalOffice.get().getBusiness().getId().equals(optionalBusiness.get().getId())) {
            throw new BadParameterValueException("Office with id of " + officeId
                    + " does not belong to business with id " + businessId);
        }
        Optional<CashRegister> optionalCashRegister = cashRegisterRepository.findById(cashRegisterId);
        if (!optionalCashRegister.isPresent()) {
            throw new ResourceNotFoundException("No such cash register with id " + cashRegisterId);
        }
        if (!optionalCashRegister.get().getOffice().getId().equals(optionalOffice.get().getId())) {
            throw new BadParameterValueException("Cash register with id of " + cashRegisterId
                    + " does not belong to office with id " + officeId);
        }
        cashRegisterRepository.deleteById(cashRegisterId);
        return new ApiResponse("Cash Register successfully deleted", 200);
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

    public Business getBusinessOfCurrentUser(UserPrincipal userPrincipal) {
        Optional<User> optionalUser = userService.findByUsername(userPrincipal.getUsername());
        if (!optionalUser.isPresent()) {
            throw new AppException("You did some nasty things!");
        }
        Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileRepository.findByAccountId(optionalUser.get().getId());
        if (!optionalEmployeeProfile.isPresent()) {
            throw new AppException("Well congrats, you killed the server");
        }
        return optionalEmployeeProfile.get().getBusiness();
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
