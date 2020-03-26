package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.repositories.business.CashRegisterRepository;
import org.springframework.stereotype.Service;

@Service
public class CashRegisterService {
    private final CashRegisterRepository cashRegisterRepository;

    public CashRegisterService(CashRegisterRepository cashRegisterRepository) {
        this.cashRegisterRepository = cashRegisterRepository;
    }
}
