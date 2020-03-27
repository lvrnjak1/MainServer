package ba.unsa.etf.si.mainserver.repositories.business;

import ba.unsa.etf.si.mainserver.models.business.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {
    List<CashRegister> findAllByOfficeId(Long officeId);

    void deleteCashRegisterByOfficeId(Long officeId);
}
