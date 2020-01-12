package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.IndividualAnswerDto;
import es.unizar.murcy.model.request.IndividualAnswerRequest;
import es.unizar.murcy.service.AnswerService;
import es.unizar.murcy.service.IndividualAnswerService;
import es.unizar.murcy.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public class IndividualAnswerController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private IndividualAnswerService individualAnswerService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @CrossOrigin
    @PostMapping("/api/individual_answer")
    public ResponseEntity create(HttpServletRequest request, @RequestBody IndividualAnswerRequest individualAnswerRequest) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if (individualAnswerRequest.isCreateValid().equals(Boolean.FALSE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        IndividualAnswer individualAnswer = individualAnswerRequest.toEntity(answerService, questionService);
        individualAnswerService.create(individualAnswer);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/{id}")
    public ResponseEntity fetchIndividualAnswerById(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<IndividualAnswer> individualAnswerOptional = individualAnswerService.findById(id);

        if (!individualAnswerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new IndividualAnswerDto((individualAnswerOptional.get())));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/list")
    public ResponseEntity fetchIndividualAnswersByQuestionId(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        List<IndividualAnswer> individualAnswers = individualAnswerService.findIndividualAnswersByQuestionId(id);

        if (individualAnswers!=null&&!individualAnswers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body((individualAnswers));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/list")
    public ResponseEntity fetchIndividualAnswersByAnswerId(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        List<IndividualAnswer> individualAnswers = individualAnswerService.findIndividualAnswersByAnswerId(id);

        if (individualAnswers!=null&&!individualAnswers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body((individualAnswers));
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

        Optional<IndividualAnswer> individualAnswerOptional = individualAnswerService.findById(id);

        if (!individualAnswerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            individualAnswerService.delete(individualAnswerOptional.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));


    }

    @CrossOrigin
    @PutMapping(value = "/api/quiz/{id}")
    public ResponseEntity update(HttpServletRequest request, @RequestBody IndividualAnswerRequest individualAnswerRequest, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<IndividualAnswer> individualAnswerOptional = individualAnswerService.findById(id);

        if (!individualAnswerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {

            if (individualAnswerRequest.getTimeInMillis() != 0) {
                individualAnswerOptional.get().setTimeInMillis(individualAnswerRequest.getTimeInMillis());
            }

            if (individualAnswerRequest.getPoints() != 0) {
                individualAnswerOptional.get().setPoints(individualAnswerRequest.getPoints());
            }

            long idQuestion=individualAnswerRequest.getQuestionId();
            Optional<Question> questionOptional=questionService.findById(idQuestion);

            if (!questionOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
            }

            individualAnswerOptional.get().setQuestion(questionOptional.get());

            long idAnswer=individualAnswerRequest.getAnswerId();
            Optional<Answer> answerOptional=answerService.findById(idAnswer);

            if (!answerOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
            }

            individualAnswerOptional.get().setAnswer(answerOptional.get());

            return ResponseEntity.status(HttpStatus.CREATED).body(new IndividualAnswerDto(((individualAnswerService.
                    update(individualAnswerOptional.get())))));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }
}
