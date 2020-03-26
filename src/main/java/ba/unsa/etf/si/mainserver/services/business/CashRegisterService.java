package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.pr.Question;
import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CashRegisterService {
    private final CashRegisterRepository cashRegisterRepository;

    public CashRegisterService(CashRegisterRepository cashRegisterRepository) {
        this.cashRegisterRepository = cashRegisterRepository;
    }

    public CashRegister save(CashRegister cashRegister) {
        return cashRegisterRepository.save(cashRegister);
    }

    public Optional<CashRegister> findByIdInOffice(Long cashRegisterId, Office office){
        Optional<CashRegister> cashRegister = cashRegisterRepository.findById(cashRegisterId);
        if (!cashRegister.isPresent()){
            throw new AppException("CashRegister with id " + cashRegisterId + " doesn't exist");
        }
        if(office.getCashRegisters().stream().filter(p -> p.getId() == cashRegisterId)
                .collect(Collectors.toList()).isEmpty()){
            throw new AppException("CashRegister with id " + cashRegisterId + " doesn't exist in office with id " + office.getId());
        }
        return  cashRegister;
    }

    public void delete(CashRegister cashRegister) {
        cashRegisterRepository.delete(cashRegister);
    }



}
