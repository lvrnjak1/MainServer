package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.models.PDV;
import ba.unsa.etf.si.mainserver.repositories.PDVRepository;
import ba.unsa.etf.si.mainserver.requests.PDVRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.PDVResponse;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pdv")
@CrossOrigin(origins = "*")
public class PDVController {
    private final PDVRepository pdvRepository;

    public PDVController(PDVRepository pdvRepository) {
        this.pdvRepository = pdvRepository;
    }

    @GetMapping
    @Secured({"ROLE_ADMIN"})
    public List<PDVResponse> getAllPDVRates(){
        return pdvRepository.findAll().stream().map(PDVResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/active")
    @Secured({"ROLE_WAREMAN"})
    public List<PDVResponse> getAllActivePDVRates(){
        return pdvRepository.findAll().stream()
                .filter(PDV::isActive)
                .map(PDVResponse::new).collect(Collectors.toList());
    }

    @PostMapping("/switch")
    @Secured({"ROLE_ADMIN"})
    public ApiResponse switchPDVRateActiveState(@RequestBody PDVRequest pdvRequest){
        PDV pdv = pdvRepository.findByPdvRate(pdvRequest.getPdv())
                .orElseThrow(() -> new BadParameterValueException("PDV rate doesn't exists"));
        pdv.setActive(!pdv.isActive());
        pdvRepository.save(pdv);
        return new ApiResponse("Active state set to " + pdv.isActive(), 200);
    }

    @PostMapping
    @Secured({"ROLE_ADMIN"})
    public ApiResponse addNewPDVRate(@RequestBody PDVRequest pdvRequest){
        Optional<PDV> pdv = pdvRepository.findByPdvRate(pdvRequest.getPdv());
        if(pdv.isPresent()){
            throw  new BadParameterValueException("PDV rate already exists");
        }
        pdvRepository.save(new PDV(pdvRequest.getPdv()));
        return new ApiResponse("New PDV rate added", 200);
    }

}
