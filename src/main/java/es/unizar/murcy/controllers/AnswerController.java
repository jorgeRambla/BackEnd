package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.answer.AnswerNotFoundException;
import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.AnswerDto;
import es.unizar.murcy.service.AnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerController {

    private AuthUtilities authUtilities;
    private AnswerService answerService;

    public AnswerController(AuthUtilities authUtilities, AnswerService answerService) {
        this.authUtilities = authUtilities;
        this.answerService = answerService;
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/{id}")
    public ResponseEntity<AnswerDto> fetchAnswerById(HttpServletRequest request, @PathVariable long id) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Answer answer = answerService.findById(id).orElseThrow(AnswerNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, answer.getUser(), User.Rol.REVIEWER);

        return ResponseEntity.status(HttpStatus.OK).body(new AnswerDto(answer));
    }

    @CrossOrigin
    @DeleteMapping(value = "/api/answer/{id}")
    public ResponseEntity<String> delete(HttpServletRequest request, @PathVariable long id) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Answer answer = answerService.findById(id).orElseThrow(AnswerNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, answer.getUser(), User.Rol.REVIEWER);

        answerService.delete(answer);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpStatus.ACCEPTED.toString());
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/list")
    public ResponseEntity<List<AnswerDto>> fetchAnswersByCurrentUser(HttpServletRequest request) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.USER);

        return ResponseEntity.status(HttpStatus.OK).body(
                answerService.findAnswersByUser(requester.getId())
                        .stream()
                        .map(AnswerDto::new)
                        .collect(Collectors.toList()));
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/list/{id}")
    public ResponseEntity<List<AnswerDto>> fetchAnswersByUserId(HttpServletRequest request, @PathVariable long id) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.USER);

        final User searchedUser = authUtilities.filterUserAuthorized(requester, id, User.Rol.REVIEWER);

        return ResponseEntity.status(HttpStatus.OK).body(
                answerService.findAnswersByUser(searchedUser.getId())
                        .stream()
                        .map(AnswerDto::new)
                        .collect(Collectors.toList()));
    }
}
