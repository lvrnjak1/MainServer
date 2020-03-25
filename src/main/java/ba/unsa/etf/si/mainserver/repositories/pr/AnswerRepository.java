package ba.unsa.etf.si.mainserver.repositories.pr;

import ba.unsa.etf.si.mainserver.models.pr.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByQuestionId(Long questionId);
}
