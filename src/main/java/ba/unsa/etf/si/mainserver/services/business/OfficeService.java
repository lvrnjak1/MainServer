package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfficeService {
    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    public Office save(Office office) {
        return officeRepository.save(office);
    }

    public Optional<Office> findById(Long officeId){
        return officeRepository.findById(officeId);
    }

    public Optional<Office> findByIdInBusiness(Long officeId, Business business){
        Optional<Office> office = officeRepository.findById(officeId);
        if (!office.isPresent()){
            throw new AppException("Office with id " + officeId + " doesn't exist");
        }
        if(business.getOffices().stream().filter(p -> p.getId() == officeId)
                .collect(Collectors.toList()).isEmpty()){
            throw new AppException("Office with id " + officeId + " doesn't exist in business with id " + business.getId());
        }
        return  office;
    }


    public void delete(Office office) {
        officeRepository.delete(office);
    }


    public List<Office> findByBusiness(Business business) {
        return officeRepository.findByBusiness(business);
    }
}
