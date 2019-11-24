package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.QuestionDto;
import es.unizar.murcy.model.request.QuestionRequest;
import es.unizar.murcy.service.QuestionService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController(value = "/api/question")
public class QuestionController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private QuestionService questionService;

    @CrossOrigin
    @PostMapping
    public ResponseEntity create(HttpServletRequest request, @RequestBody QuestionRequest questionRequest) {
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if(!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if(!questionRequest.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        Question question = questionRequest.toEntity();
        question.setUser(user.get());

        questionService.create(question);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping("/list")
    public ResponseEntity fetchCurrentUserQuestionList(HttpServletRequest request) {
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        return ResponseEntity.status(HttpStatus.OK).body(questionService.findQuestionsByOwner(user.get()).stream().map(QuestionDto::new).collect(Collectors.toList()));
    }

    @CrossOrigin
    @GetMapping("/list/{id}")
    public ResponseEntity fetchCurrentUserQuestionList(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if(id == user.get().getId() || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(questionService.findQuestionsByOwner(user.get()).stream().map(QuestionDto::new).collect(Collectors.toList()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }
}
