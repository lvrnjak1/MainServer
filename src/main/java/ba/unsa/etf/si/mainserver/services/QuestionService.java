package ba.unsa.etf.si.mainserver.services;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.pr.Answer;
import ba.unsa.etf.si.mainserver.models.pr.Question;
import ba.unsa.etf.si.mainserver.repositories.pr.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question save(Question question){
        return questionRepository.save(question);
    }

    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findQuestionById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    public Question saveAnswer(Long questionId, Answer answer) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (!question.isPresent()){
            throw new  AppException("Question with id " + questionId + " doesn't exist");
        }

        question.get().setAnswer(answer);
        answer.setQuestion(question.get());
        return questionRepository.save(question.get());
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }
}
