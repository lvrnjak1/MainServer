package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
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
    private final CashRegisterRepository cashRegisterRepository;
    private final CashRegisterService cashRegisterService;

    public OfficeService(OfficeRepository officeRepository, BusinessRepository businessRepository, CashRegisterRepository cashRegisterRepository, CashRegisterService cashRegisterService) {
        this.officeRepository = officeRepository;
        this.businessRepository = businessRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.cashRegisterService = cashRegisterService;
    }

    public Office save(Office office) {
        return officeRepository.save(office);
    }

    public Optional<Office> findById(Long officeId) {
        return officeRepository.findById(officeId);
    }


    public void delete(Office office) {
        officeRepository.delete(office);
    }


    public List<Office> findByBusiness(Business business) {
        return officeRepository.findByBusiness(business);
    }

    public ApiResponse deleteOfficeOfBusiness(Long officeId, Long businessId) {
        Optional<Business> optionalBusiness = businessRepository.findById(businessId);
        if (!optionalBusiness.isPresent()) {
            throw new ResourceNotFoundException("Business with id " + businessId + " not found");
        }
        Optional<Office> optionalOffice = officeRepository.findById(officeId);
        if (!optionalOffice.isPresent()) {
            throw new ResourceNotFoundException("Office with id " + officeId + " not found");
        }
        if (!optionalOffice.get().getBusiness().getId().equals(optionalBusiness.get().getId())) {
            throw new BadParameterValueException("Office with id " + officeId + " does not belong to business with id " + businessId);
        }
        //cashRegisterRepository.deleteCashRegisterByOfficeId(officeId);
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

    public Optional<Office> findByManager(EmployeeProfile employeeProfile) {
        return officeRepository.findByManager(employeeProfile);
    }
}
