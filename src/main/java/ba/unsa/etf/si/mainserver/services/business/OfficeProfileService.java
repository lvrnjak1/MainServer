package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class OfficeProfileService {
    private final OfficeProfileRepository officeProfileRepository;

    public OfficeProfileService(OfficeProfileRepository officeProfileRepository) {
        this.officeProfileRepository = officeProfileRepository;
    }
}
