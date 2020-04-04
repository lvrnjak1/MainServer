package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.pr.OfficeReview;
import ba.unsa.etf.si.mainserver.requests.pr.OfficeReviewRequest;
import ba.unsa.etf.si.mainserver.responses.pr.OfficeReviewResponse;
import ba.unsa.etf.si.mainserver.services.business.OfficeService;
import ba.unsa.etf.si.mainserver.services.pr.OfficeReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final OfficeReviewService officeReviewService;
    private final OfficeService officeService;

    public ReviewController(OfficeReviewService officeReviewService,
                            OfficeService officeService) {
        this.officeReviewService = officeReviewService;
        this.officeService = officeService;
    }

    @PostMapping("/{officeId}")
    public OfficeReviewResponse addReview(@PathVariable Long officeId,
                                          @RequestBody OfficeReviewRequest officeReviewRequest){
        Optional<Office> officeOptional = officeService.findById(officeId);
        if(!officeOptional.isPresent()){
            throw new ResourceNotFoundException("Office doesn't exist");
        }

        OfficeReview officeReview = new OfficeReview(
                officeReviewRequest.getFirstName(),
                officeReviewRequest.getLastName(),
                officeReviewRequest.getEmail(),
                officeReviewRequest.getStarReview(),
                officeReviewRequest.getText(),
                officeOptional.get()
        );

        return new OfficeReviewResponse(officeReviewService.save(officeReview));
    }

    @GetMapping
    public List<OfficeReviewResponse> getAllReviews(){
        return officeReviewService.findAll()
                .stream()
                .map(OfficeReviewResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{officeId}")
    public List<OfficeReviewResponse> getAllReviewsForOffice(@PathVariable Long officeId){
        return officeReviewService.findAllForOffice(officeId)
                .stream()
                .map(OfficeReviewResponse::new)
                .collect(Collectors.toList());
    }
}
