package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.pr.Answer;
import ba.unsa.etf.si.mainserver.models.pr.Question;
import ba.unsa.etf.si.mainserver.models.pr.QuestionAuthor;
import ba.unsa.etf.si.mainserver.requests.pr.AnswerRequest;
import ba.unsa.etf.si.mainserver.requests.pr.QuestionRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.pr.QuestionResponse;
import ba.unsa.etf.si.mainserver.services.pr.AnswerService;
import ba.unsa.etf.si.mainserver.services.pr.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;

    public QuestionController(QuestionService questionService, AnswerService answerService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }

    @PostMapping
    public QuestionResponse saveQuestion(@RequestBody QuestionRequest questionRequest){
        QuestionAuthor questionAuthor= new QuestionAuthor(questionRequest.getNameSurname(), questionRequest.getEmail(), null);
        Question question = null;
        try {
            question = new Question(questionRequest.getText(), questionAuthor, questionRequest.getDateFromString(), questionRequest.getTimeFromString());
        } catch (ParseException e) {
            throw new AppException("Invalid date or time format");
        }
        questionAuthor.setQuestion(question);
        return new QuestionResponse(questionService.save(question));
    }

    @GetMapping
    public List<QuestionResponse> getAllQuestions(){
        return questionService.getAll().stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/answered")
    @Secured({"ROLE_PRW", "ROLE_ADMIN"})
    public List<QuestionResponse> getAllAnsweredQuestions(){
        return questionService.getAll().stream().
                filter(question -> question.getAnswer() != null)
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/unanswered")
    @Secured({"ROLE_PRW", "ROLE_ADMIN"})
    public List<QuestionResponse> getAllUnansweredQuestions(){
        return questionService.getAll().stream().
                filter(question -> question.getAnswer() == null)
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{questionId}")
    @Secured({"ROLE_PRW", "ROLE_ADMIN"})
    public ResponseEntity<?> deleteQuestion(@PathVariable("questionId") Long questionId){
        return questionService.findQuestionById(questionId)
                .map(question -> {
                    questionService.delete(question);
                    return ResponseEntity.ok(new ApiResponse("Question successfully deleted", 200));
                }).orElseThrow(() -> new AppException("Question with id " + questionId + " doesn't exist"));
    }

    @PostMapping("/{questionId}/answer")
    @Secured({"ROLE_PRW", "ROLE_ADMIN"})
    public QuestionResponse saveAnswerToQuestion(@RequestBody AnswerRequest answerRequest, @PathVariable("questionId") Long questionId){
        return questionService.findQuestionById(questionId).map(question -> {
            Answer answer = new Answer(answerRequest.getText());
            return new QuestionResponse(questionService.saveAnswer(questionId, answer));
        }).orElseThrow(() -> new AppException("Question with id " + questionId + " doesn't exist"));
    }

    @DeleteMapping("/{questionId}/answer")
    @Secured({"ROLE_PRW", "ROLE_ADMIN"})
    public ResponseEntity<?> deleteAnswer(@PathVariable("questionId") Long questionId){

        return answerService.findByQuestionId(questionId).map(answer -> questionService.findQuestionById(questionId).map(question -> {
            question.setAnswer(null);
            questionService.save(question);
            answerService.delete(answer);
            return ResponseEntity.ok(new ApiResponse("Answer successfully deleted", 200));
        }).orElseThrow(()->new AppException("Question with id " + questionId + " doesn't exist"))).orElseThrow(() -> new AppException("Question with id " + questionId + " doesn't have an answer"));
    }

}
