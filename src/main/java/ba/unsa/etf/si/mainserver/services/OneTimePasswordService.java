package ba.unsa.etf.si.mainserver.services;

import ba.unsa.etf.si.mainserver.models.auth.OneTimePassword;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.repositories.auth.OneTimePasswordRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OneTimePasswordService {
    private final OneTimePasswordRepository oneTimePasswordRepository;


    public OneTimePasswordService(OneTimePasswordRepository oneTimePasswordRepository) {
        this.oneTimePasswordRepository = oneTimePasswordRepository;
    }

    public void createOneTimePassword(User user, String password) {
        OneTimePassword otp = new OneTimePassword(password, user);
        oneTimePasswordRepository.save(otp);
    }

    public Optional<OneTimePassword> findByUser(User user){
        return oneTimePasswordRepository.findByUser(user);
    }

    public void delete(OneTimePassword otp){
        oneTimePasswordRepository.delete(otp);
    }

    public void save(OneTimePassword otp){oneTimePasswordRepository.save(otp);}
}
