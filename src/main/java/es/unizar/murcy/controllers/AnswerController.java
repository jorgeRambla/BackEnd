package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.answer.AnswerBadRequestException;
import es.unizar.murcy.exceptions.answer.AnswerNotFoundException;
import es.unizar.murcy.exceptions.user.UserUnauthorizedException;
import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.AnswerDto;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.request.AnswerRequest;
import es.unizar.murcy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class AnswerController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private AnswerService answerService;

    @CrossOrigin
    @GetMapping(value = "/api/answer/{id}")
    public ResponseEntity<AnswerDto> fetchAnswerById(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Answer answer = answerService.findById(id).orElseThrow(AnswerNotFoundException::new);

        if (answer.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new AnswerDto(answer));
        }

        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @DeleteMapping(value = "/api/answer/{id}")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Answer answer = answerService.findById(id).orElseThrow(AnswerNotFoundException::new);

        if (answer.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {
            answerService.delete(answer);

            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }

        throw new UserUnauthorizedException();
    }
}
