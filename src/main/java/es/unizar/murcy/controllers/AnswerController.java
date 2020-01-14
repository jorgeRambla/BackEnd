package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.answer.AnswerBadRequestException;
import es.unizar.murcy.exceptions.answer.AnswerNotFoundException;
import es.unizar.murcy.exceptions.user.UserNotFoundException;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnswerController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

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

    @CrossOrigin
    @GetMapping(value = "/api/answer/list")
    public ResponseEntity<List<AnswerDto>> fetchAnswersByCurrentUser(HttpServletRequest request) {
        User user = authUtilities.getUserFromRequest(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                answerService.findAnswersByUser(user.getId())
                        .stream()
                        .map(AnswerDto::new)
                        .collect(Collectors.toList()));
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/list/{id}")
    public ResponseEntity<List<AnswerDto>> fetchAnswersByUserId(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request);

        if(user.getId() == id || user.getRoles().contains(User.Rol.REVIEWER)) {
            User fetchUser = userService.findUserById(id).orElseThrow(UserNotFoundException::new);
            return ResponseEntity.status(HttpStatus.OK).body(
                    answerService.findAnswersByUser(fetchUser.getId())
                            .stream()
                            .map(AnswerDto::new)
                            .collect(Collectors.toList()));
        }
        throw new UserUnauthorizedException();
    }
}
