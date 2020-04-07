package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.business.CashRegisterResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CashRegisterService {
    private final CashRegisterRepository cashRegisterRepository;
    private final OfficeRepository officeRepository;
    private final BusinessRepository businessRepository;

    public CashRegisterService(CashRegisterRepository cashRegisterRepository, OfficeRepository officeRepository, BusinessRepository businessRepository) {
        this.cashRegisterRepository = cashRegisterRepository;
        this.officeRepository = officeRepository;
        this.businessRepository = businessRepository;
    }

    public CashRegister save(CashRegister cashRegister) {
        return cashRegisterRepository.save(cashRegister);
    }

    public void delete(CashRegister cashRegister) {
        cashRegisterRepository.delete(cashRegister);
    }

    public CashRegister findCashRegisterById(Long cashRegisterId, Long officeId, Long businessId){
        Office office = findOffice(officeId, businessId);
        Optional<CashRegister> optionalCashRegister = cashRegisterRepository.findById(cashRegisterId);
        if (!optionalCashRegister.isPresent()) {
            throw new ResourceNotFoundException("No such cash register with id " + cashRegisterId);
        }
        if (!optionalCashRegister.get().getOffice().getId().equals(office.getId())) {
            throw new BadParameterValueException("Cash register with id of " + cashRegisterId
                    + " does not belong to office with id " + officeId);
        }
        return  optionalCashRegister.get();
    }

    public ApiResponse deleteCashRegisterByIdFromOfficeOfBusiness(Long cashRegisterId, Long officeId, Long businessId) {
        CashRegister cashRegister = findCashRegisterById(cashRegisterId, officeId, businessId);
        cashRegisterRepository.delete(cashRegister);
        return new ApiResponse("Cash Register successfully deleted", 200);
    }

    public CashRegister createCashRegisterInOfficeOfBusiness(Long officeId, Long businessId, String name) {
        Office office = findOffice(officeId, businessId);
        CashRegister cashRegister = new CashRegister(office, name);
        return cashRegisterRepository.save(cashRegister);
    }

    public List<CashRegister> getAllCashRegistersByOfficeId(Long officeId) {
        return cashRegisterRepository.findAllByOfficeId(officeId);
    }

    public List<CashRegisterResponse> getAllCashRegisterResponsesByOfficeId(Long officeId) {
        return cashRegisterRepository.findAllByOfficeId(officeId).stream().map(CashRegisterResponse::new).collect(Collectors.toList());
    }

    public Office findOffice(Long officeId, Long businessId) {
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
}
