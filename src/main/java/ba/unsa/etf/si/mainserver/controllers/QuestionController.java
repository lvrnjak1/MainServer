package ba.unsa.etf.si.mainserver.controllers;

import ba.unsa.etf.si.mainserver.configurations.Actions;
import ba.unsa.etf.si.mainserver.exceptions.AppException;
import ba.unsa.etf.si.mainserver.models.auth.User;
import ba.unsa.etf.si.mainserver.models.pr.Answer;
import ba.unsa.etf.si.mainserver.models.pr.Question;
import ba.unsa.etf.si.mainserver.models.pr.QuestionAuthor;
import ba.unsa.etf.si.mainserver.requests.pr.AnswerRequest;
import ba.unsa.etf.si.mainserver.requests.pr.QuestionRequest;
import ba.unsa.etf.si.mainserver.responses.ApiResponse;
import ba.unsa.etf.si.mainserver.responses.pr.QuestionResponse;
import ba.unsa.etf.si.mainserver.security.CurrentUser;
import ba.unsa.etf.si.mainserver.security.UserPrincipal;
import ba.unsa.etf.si.mainserver.services.UserService;
import ba.unsa.etf.si.mainserver.services.admin.logs.LogServerService;
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
@CrossOrigin(origins = "*")
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final LogServerService logServerService;

    public QuestionController(QuestionService questionService, AnswerService answerService, UserService userService, LogServerService logServerService) {
        this.questionService = questionService;
        this.answerService = answerService;
        this.userService = userService;
        this.logServerService = logServerService;
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
    public ResponseEntity<?> deleteQuestion(
            @PathVariable("questionId") Long questionId,
            @CurrentUser UserPrincipal userPrincipal){
        return questionService.findQuestionById(questionId)
                .map(question -> {
                    questionService.delete(question);
                    // DO NOT EDIT THIS CODE BELOW, EVER
                    logServerService.documentAction(
                            userPrincipal.getUsername(),
                            Actions.DELETE_QUESTION_ACTION_NAME,
                            "question",
                            "User " + userPrincipal.getUsername() + " has deleted the question: " + question.getText()
                    );
                    // DO NOT EDIT THIS CODE ABOVE, EVER
                    return ResponseEntity.ok(new ApiResponse("Question successfully deleted", 200));
                }).orElseThrow(() -> new AppException("Question with id " + questionId + " doesn't exist"));
    }

    @PostMapping("/{questionId}/answer")
    @Secured({"ROLE_PRP", "ROLE_ADMIN"})
    public QuestionResponse saveAnswerToQuestion(@RequestBody AnswerRequest answerRequest,
                                                 @PathVariable("questionId") Long questionId,
                                                 @CurrentUser UserPrincipal userPrincipal){
        return questionService.findQuestionById(questionId).map(question -> {
            User user = userService.findUserByUsername(userPrincipal.getUsername());
            Answer answer = new Answer(answerRequest.getText(), user);
            // DO NOT EDIT THIS CODE BELOW, EVER
            logServerService.documentAction(
                    userPrincipal.getUsername(),
                    Actions.SAVE_ANSWER_ACTION_NAME,
                    "answer",
                    "User " + userPrincipal.getUsername() + " has answered question " + question.getText() + " with: " + answer.getText()
            );
            // DO NOT EDIT THIS CODE ABOVE, EVER
            return new QuestionResponse(questionService.saveAnswer(questionId, answer));
        }).orElseThrow(() -> new AppException("Question with id " + questionId + " doesn't exist"));
    }

    @DeleteMapping("/{questionId}/answer")
    @Secured({"ROLE_PRW", "ROLE_ADMIN"})
    public ResponseEntity<?> deleteAnswer(
            @PathVariable("questionId") Long questionId,
            @CurrentUser UserPrincipal userPrincipal){

        return answerService.findByQuestionId(questionId).map(answer -> questionService.findQuestionById(questionId).map(question -> {
            question.setAnswer(null);
            questionService.save(question);
            answerService.delete(answer);
            // DO NOT EDIT THIS CODE BELOW, EVER
            logServerService.documentAction(
                    userPrincipal.getUsername(),
                    Actions.DELETE_ANSWER_ACTION_NAME,
                    "answer",
                    "User " + userPrincipal.getUsername() + " has deleted answer to question " + question.getText()
            );
            // DO NOT EDIT THIS CODE ABOVE, EVER
            return ResponseEntity.ok(new ApiResponse("Answer successfully deleted", 200));
        }).orElseThrow(()->new AppException("Question with id " + questionId + " doesn't exist"))).orElseThrow(() -> new AppException("Question with id " + questionId + " doesn't have an answer"));
    }

}
