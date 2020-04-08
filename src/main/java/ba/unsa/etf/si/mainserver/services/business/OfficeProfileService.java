package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.BadParameterValueException;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.OfficeProfile;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.business.OfficeProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OfficeProfileService {
    private final OfficeProfileRepository officeProfileRepository;

    public OfficeProfileService(OfficeProfileRepository officeProfileRepository) {
        this.officeProfileRepository = officeProfileRepository;
    }

    public void assignEmployeeToOffice(EmployeeProfile employeeProfile, Office office){
        Optional<OfficeProfile> optionalOfficeProfile = officeProfileRepository.findByEmployeeIdAndOfficeId(employeeProfile.getId(), office.getId());
        if(optionalOfficeProfile.isPresent()){
            throw new BadParameterValueException("Employee is already hired");
        }

        OfficeProfile officeProfile = new OfficeProfile(office, employeeProfile);
        officeProfileRepository.save(officeProfile);
    }
}
