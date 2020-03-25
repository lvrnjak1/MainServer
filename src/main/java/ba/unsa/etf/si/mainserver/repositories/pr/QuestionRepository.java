package ba.unsa.etf.si.mainserver.repositories.pr;

import ba.unsa.etf.si.mainserver.models.pr.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
