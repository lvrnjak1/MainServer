package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.repositories.business.ContactInformationRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactInformationService {
    private final ContactInformationRepository contactInformationRepository;

    public ContactInformationService(ContactInformationRepository contactInformationRepository) {
        this.contactInformationRepository = contactInformationRepository;
    }
}
