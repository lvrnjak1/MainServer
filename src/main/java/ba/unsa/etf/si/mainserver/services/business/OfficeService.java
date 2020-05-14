package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.business.OfficeResponse;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfficeService {
    private final OfficeRepository officeRepository;
    private final BusinessRepository businessRepository;
    private final CashRegisterService cashRegisterService;
    private final UserService userService;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeActivityRepository employeeActivityRepository;

    public OfficeService(OfficeRepository officeRepository,
                         BusinessRepository businessRepository,
                         CashRegisterService cashRegisterService,
                         UserService userService,
                         EmployeeProfileRepository employeeProfileRepository,
                         EmployeeActivityRepository employeeActivityRepository) {
        this.officeRepository = officeRepository;
        this.businessRepository = businessRepository;
        this.cashRegisterService = cashRegisterService;
        this.userService = userService;
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeActivityRepository = employeeActivityRepository;
    }

    public OfficeResponse getOfficeResponseByOfficeId(Long officeId) {
        Optional<Office> optionalOffice = officeRepository.findById(officeId);
        if (!optionalOffice.isPresent()) {
            throw new ResourceNotFoundException("No such office with id " + officeId);
        }
        Office office = optionalOffice.get();
        return new OfficeResponse(
          office.getId(),
          office.getContactInformation().getAddress(),
          office.getContactInformation().getCity(),
          office.getContactInformation().getCountry(),
          office.getContactInformation().getEmail(),
          office.getContactInformation().getPhoneNumber(),
          office.getStringStart(),
          office.getStringEnd(),
          office.getLanguageName().name(),
          office.getMaxNumberCashRegisters(),
          null,
          null,
                null
        );
    }

    public Office findOfficeById(Long officeId, Long businessId){
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
        return optionalOffice.get();
    }

    public Office save(Office office) {
        return officeRepository.save(office);
    }

    public Optional<Office> findById(Long officeId) {
        return officeRepository.findById(officeId);
    }

    public Office findByIdOrThrow(Long officeId){
        return findById(officeId).orElseThrow(
                () -> new ResourceNotFoundException("Office with id " + officeId + " doesn't exist"));
    }

    public void delete(Office office) {
        officeRepository.delete(office);
    }


    public List<Office> findAllByBusiness(Business business) {
        return officeRepository.findByBusiness(business);
    }

    public ApiResponse deleteOfficeOfBusiness(Long officeId, Long businessId) {
        Office office = findOfficeById(officeId, businessId);
        List<CashRegister> cashRegisters = cashRegisterService.getAllCashRegistersByOfficeId(officeId);
        if(!cashRegisters.isEmpty()){
            cashRegisters.forEach(cashRegisterService::delete);
        }
        officeRepository.deleteById(officeId);
        return new ApiResponse("Office successfully deleted!", 200);
    }

    public List<OfficeResponse> getAllOfficeResponsesByBusinessId(Long businessId) {
        return officeRepository
                .findAllByBusinessId(businessId)
                .stream()
                .map(
                        office -> new OfficeResponse(
                                office,cashRegisterService.getAllCashRegisterResponsesByOfficeId(office.getId()), null)
                ).collect(Collectors.toList());
    }

    public Optional<Office> findByManager(EmployeeProfile employeeProfile) {
        return officeRepository.findByManager(employeeProfile);
    }

    public List<Office> findAll() {
        return officeRepository.findAll();
    }

    public void validateBusiness(Office office, Business business) {
        if (!office.getBusiness().getId().equals(business.getId())) {
            throw new UnauthorizedException("Not your office");
        }
    }

    public int countCashRegsitersInOffice(Long officeId){
        return cashRegisterService.getAllCashRegistersByOfficeId(officeId).size();
    }

    public Office findByManager(UserPrincipal userPrincipal) {
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
        return findByManager(optionalEmployeeProfile.get())
                .orElseThrow(() -> new ResourceNotFoundException("Employee is not office manager"));
    }
}
