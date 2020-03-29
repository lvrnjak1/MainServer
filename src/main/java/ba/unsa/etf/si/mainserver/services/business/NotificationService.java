package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Business;
import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import ba.unsa.etf.si.mainserver.models.business.Notification;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.repositories.business.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository){
        this.notificationRepository = notificationRepository;
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> findAllByBusinessId(Long businessId){
        return notificationRepository.findAllByBusinessId(businessId);
    }

    public Notification findByIdInBusiness(Long notificationId, Business business) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (!notificationOptional.isPresent()) {
            throw new ResourceNotFoundException("Notification with id " + notificationId + " doesn't exist");
        }
        if (!notificationOptional.get().getBusiness().getId().equals(business.getId())) {
            throw new BadParameterValueException("Notification with id " + notificationId
                    + "does not belong to business with id " + business.getId());
        }
        return notificationOptional.get();
    }

}
