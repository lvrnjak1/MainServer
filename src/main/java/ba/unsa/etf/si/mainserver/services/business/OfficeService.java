package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.exceptions.UnauthorizedException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.business.OfficeResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfficeService {
    private final OfficeRepository officeRepository;
    private final BusinessRepository businessRepository;
    private final CashRegisterService cashRegisterService;

    public OfficeService(OfficeRepository officeRepository, BusinessRepository businessRepository,CashRegisterService cashRegisterService) {
        this.officeRepository = officeRepository;
        this.businessRepository = businessRepository;
        this.cashRegisterService = cashRegisterService;
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
                                office,cashRegisterService.getAllCashRegisterResponsesByOfficeId(office.getId()))
                ).collect(Collectors.toList());
    }

    public List<Office> findAllByManager(EmployeeProfile employeeProfile) {
        return officeRepository.findAllByManager(employeeProfile);
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
}
