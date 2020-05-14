package ba.unsa.etf.si.mainserver.services.business;

import ba.unsa.etf.si.mainserver.exceptions.ResourceNotFoundException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.business.Office;
import ba.unsa.etf.si.mainserver.models.business.ServerOffice;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeActivity;
import ba.unsa.etf.si.mainserver.models.employees.EmployeeProfile;
import ba.unsa.etf.si.mainserver.repositories.EmployeeActivityRepository;
import ba.unsa.etf.si.mainserver.repositories.business.EmployeeProfileRepository;
import ba.unsa.etf.si.mainserver.repositories.business.ServerOfficeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServerOfficeService {
    private final ServerOfficeRepository serverOfficeRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeActivityRepository employeeActivityRepository;

    public ServerOfficeService(ServerOfficeRepository serverOfficeRepository,
                               EmployeeProfileRepository employeeProfileRepository,
                               EmployeeActivityRepository employeeActivityRepository) {
        this.serverOfficeRepository = serverOfficeRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeActivityRepository = employeeActivityRepository;
    }

    public boolean isServer(EmployeeProfile employeeProfile){
        return isServer(employeeProfile.getAccount());
    }

    public boolean isServer(User user){
        return serverOfficeRepository.findByUser(user).isPresent();
    }

    public Optional<Office> findOfficeByServer(User user){
        Optional<ServerOffice> serverOfficeOptional = serverOfficeRepository.findByUser(user);
        return serverOfficeOptional.map(ServerOffice::getOffice);

    }

    public Optional<Office> findOfficeByServer(EmployeeProfile employeeProfile){
        return findOfficeByServer(employeeProfile.getAccount());
    }

    public User findServerByOffice(Long officeId){
        Optional<ServerOffice> serverOffice = serverOfficeRepository.findByOffice_Id(officeId);
        if(!serverOffice.isPresent()){
            throw new ResourceNotFoundException("Server doesn't exist for this office");
        }
        return serverOffice.get().getUser();
    }

    public void fireServer(Long officeId) {
        User server = findServerByOffice(officeId);
        Optional<EmployeeProfile> employeeProfile =
                employeeProfileRepository.findByAccount_Id(server.getId());
        if(!employeeProfile.isPresent()){
           throw new ResourceNotFoundException("Server doesn't exist for office");
        }
        EmployeeActivity employeeActivity = new EmployeeActivity(employeeProfile.get(), server);
        employeeActivityRepository.save(employeeActivity);
    }
}
