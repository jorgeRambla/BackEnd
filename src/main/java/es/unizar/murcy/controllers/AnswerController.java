package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.AnswerDto;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.request.AnswerRequest;
import es.unizar.murcy.service.AnswerService;
import es.unizar.murcy.service.IndividualAnswerService;
import es.unizar.murcy.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnswerController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private IndividualAnswerService individualAnswerService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuizService quizService;

    @CrossOrigin
    @PostMapping("/api/answer")
    public ResponseEntity create(HttpServletRequest request, @RequestBody AnswerRequest answerRequest) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if (answerRequest.isCreateValid().equals(Boolean.FALSE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        if (answerService.existsByTitle(answerRequest.getTitle())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST,
                    "Ya existe una respuesta con ese titulo."));
        }

        Answer answer = answerRequest.toEntity(individualAnswerService);
        answerService.create(answer);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/{id}")
    public ResponseEntity fetchAnswerById(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<Answer> optionalAnswer = answerService.findById(id);

        if (!optionalAnswer.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new AnswerDto(optionalAnswer.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/list")
    public ResponseEntity fetchAnswersByQuizId(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<Quiz> optionalQuiz = quizService.findById(id);

        if (!optionalQuiz.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        List<Answer> answerList = answerService.findAnswersByQuizId(id);

        if (!(answerList!=null)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(answerList);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @DeleteMapping(value = "/api/answer/{id}")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<Answer> optionalAnswer = answerService.findById(id);

        if (!optionalAnswer.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            answerService.delete(optionalAnswer.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @PutMapping(value = "/api/quiz/{id}")
    public ResponseEntity update(HttpServletRequest request, @RequestBody AnswerRequest answerRequest, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<Answer> optionalAnswer = answerService.findById(id);

        if (!optionalAnswer.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {

            if (answerRequest.getTitle() != null && !answerRequest.getTitle().equals("")) {
                optionalAnswer.get().setTitle(answerRequest.getTitle());
            }

            if (answerRequest.getDescription() != null) {
                optionalAnswer.get().setDescription(answerRequest.getDescription());
            }

            if (answerRequest.getIndividualAnswersIds() != null && !answerRequest.getIndividualAnswersIds().isEmpty()) {
                optionalAnswer.get().setIndividualAnswers(answerRequest.getIndividualAnswersIds().stream()
                        .map(individualAnswerService::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new AnswerDto((answerService.update(optionalAnswer.get()))));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

}
