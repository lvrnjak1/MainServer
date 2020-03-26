package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import org.springframework.stereotype.Service;

@Service
public class OfficeService {
    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }
}
