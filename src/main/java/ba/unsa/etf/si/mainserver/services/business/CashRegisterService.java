package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import ba.unsa.etf.si.mainserver.responses.business.CashRegisterResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CashRegisterService {
    private final CashRegisterRepository cashRegisterRepository;
    private final BusinessRepository businessRepository;
    private final OfficeRepository officeRepository;

    public CashRegisterService(CashRegisterRepository cashRegisterRepository, BusinessRepository businessRepository, OfficeRepository officeRepository) {
        this.cashRegisterRepository = cashRegisterRepository;
        this.businessRepository = businessRepository;
        this.officeRepository = officeRepository;
    }

    public CashRegister save(CashRegister cashRegister) {
        return cashRegisterRepository.save(cashRegister);
    }

    public CashRegister findByIdInOffice(Long cashRegisterId, Office office) {
        Optional<CashRegister> cashRegister = cashRegisterRepository.findById(cashRegisterId);
        if (!cashRegister.isPresent()) {
            throw new ResourceNotFoundException("CashRegister with id " + cashRegisterId + " doesn't exist");
        }
        if (!cashRegister.get().getOffice().getId().equals(office.getId())) {
            throw new BadParameterValueException("Cash register with id " + cashRegisterId
                    + "does not belong to office with id " + office.getId());
        }
        return cashRegister.get();
    }

    public void delete(CashRegister cashRegister) {
        cashRegisterRepository.delete(cashRegister);
    }

    public CashRegister createCashRegisterInOfficeOfBusiness(Long officeId, Long businessId) {
        Optional<Business> optionalBusiness = businessRepository.findById(businessId);
        if (!optionalBusiness.isPresent()) {
            throw new ResourceNotFoundException("Business with id " + businessId + " not found!");
        }
        Optional<Office> optionalOffice = officeRepository.findById(officeId);
        if (!optionalOffice.isPresent()) {
            throw new ResourceNotFoundException("Office with id " + officeId + " not found!");
        }
        if (!optionalOffice.get().getBusiness().getId().equals(optionalBusiness.get().getId())) {
            throw new BadParameterValueException("Office with id " + officeId
                    + " does not belong to business with id " + businessId);
        }
        CashRegister cashRegister = new CashRegister();
        cashRegister.setOffice(optionalOffice.get());
        return cashRegisterRepository.save(cashRegister);
    }

    public List<CashRegisterResponse> getAllCashRegisterResponsesByOfficeId(Long officeId) {
        return cashRegisterRepository.findAllByOfficeId(officeId).stream().map(CashRegisterResponse::new).collect(Collectors.toList());
    }
}
