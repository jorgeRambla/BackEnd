package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.AnswerDto;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.service.AnswerService;
import es.unizar.murcy.service.IndividualAnswerService;
import es.unizar.murcy.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
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
    @GetMapping("/api/question/{id}/answers")
    public ResponseEntity fetchAnswersByQuestionId(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Question



        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new AnswerDto(optionalAnswer.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));

    }
    //TODO: controller for getting all individual answers for one question GET /api/question/{id}/anwsers -> Lista de respuestas individuales
}
