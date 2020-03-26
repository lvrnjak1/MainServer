package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {
    private final BusinessRepository businessRepository;

    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }
}
