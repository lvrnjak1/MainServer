package ba.unsa.etf.si.mainserver.repositories.pr;

import ba.unsa.etf.si.mainserver.models.pr.OfficeReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficeReviewRepository extends JpaRepository<OfficeReview, Long> {
    List<OfficeReview> findAllByOffice_Id(Long officeId);
}
