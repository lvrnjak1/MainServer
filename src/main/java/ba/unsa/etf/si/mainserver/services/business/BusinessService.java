package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.repositories.business.BusinessRepository;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final OfficeRepository officeRepository;

    public BusinessService(BusinessRepository businessRepository, OfficeRepository officeRepository) {
        this.businessRepository = businessRepository;
        this.officeRepository = officeRepository;
    }

    public Business save(Business business) {
        return businessRepository.save(business);
    }

    public List<Business> findAll() {
        return businessRepository.findAll();
    }

    public Optional<Business> findById(Long businessId){
        return businessRepository.findById(businessId);
    }

//    public Optional<Business> getBusinessByProductId(Product product){
//        Optional<Office> officeOptional = officeRepository.findById(product.getId());
//        if(officeOptional.isPresent()){
//            return businessRepository.findById(officeOptional.get().getBusiness().getId());
//        }
//
//        throw new AppException("Office with id " + product.getId() + " doesn't exist");
//    }
}
