package ba.unsa.etf.si.mainserver.services;

import ba.unsa.etf.si.mainserver.models.pr.Answer;
import ba.unsa.etf.si.mainserver.repositories.pr.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Optional<Answer> findByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }
}
