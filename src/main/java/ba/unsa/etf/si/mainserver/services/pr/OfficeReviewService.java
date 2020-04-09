package ba.unsa.etf.si.mainserver.services.pr;

import ba.unsa.etf.si.mainserver.models.pr.OfficeReview;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeRepository;
import ba.unsa.etf.si.mainserver.repositories.pr.OfficeReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfficeReviewService {
    private final OfficeReviewRepository officeReviewRepository;
    private final OfficeRepository officeRepository;

    public OfficeReviewService(OfficeReviewRepository officeReviewRepository,
                               OfficeRepository officeRepository) {
        this.officeReviewRepository = officeReviewRepository;
        this.officeRepository = officeRepository;
    }

    public OfficeReview save(OfficeReview officeReview) {
        return officeReviewRepository.save(officeReview);
    }

    public List<OfficeReview> findAll() {
        return officeReviewRepository.findAll();
    }

    public List<OfficeReview> findAllForOffice(Long officeId) {
        return officeReviewRepository.findAllByOffice_Id(officeId);
    }

    public Optional<OfficeReview> findById(Long reviewId) {
        return officeReviewRepository.findById(reviewId);
    }
}
