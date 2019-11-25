package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.QuestionDto;
import es.unizar.murcy.model.request.OptionRequest;
import es.unizar.murcy.model.request.QuestionRequest;
import es.unizar.murcy.service.QuestionService;
import es.unizar.murcy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class QuestionController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping(value = "/api/question")
    public ResponseEntity create(HttpServletRequest request, @RequestBody QuestionRequest questionRequest) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if (!questionRequest.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        Question question = questionRequest.toEntity();
        question.setUser(user.get());

        //EXPECTED: implement workflow, while -> is approved and closed
        question.setApproved(true);
        question.setClosed(true);

        questionService.create(question);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping("/api/question/list")
    public ResponseEntity fetchCurrentUserQuestionList(HttpServletRequest request) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        return ResponseEntity.status(HttpStatus.OK).body(questionService.findQuestionsByOwner(user.get()).stream().map(QuestionDto::new).collect(Collectors.toList()));
    }

    @CrossOrigin
    @GetMapping("/api/question/list/{id}")
    public ResponseEntity fetchCurrentUserQuestionListByOwnerId(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if (id == user.get().getId()) {
            return ResponseEntity.status(HttpStatus.OK).body(questionService.findQuestionsByOwner(user.get()).stream().map(QuestionDto::new).collect(Collectors.toList()));
        }

        Optional<User> fetchedUser = userService.findUserById(id);

        if (user.get().getRoles().contains(User.Rol.REVIEWER)) {
            if (!fetchedUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(questionService.findQuestionsByOwner(user.get()).stream().map(QuestionDto::new).collect(Collectors.toList()));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @GetMapping("/api/question/{id}")
    public ResponseEntity fetchQuestionById(HttpServletRequest request, @PathVariable long id) {
        Optional<Question> question = questionService.findById(id);

        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if (!question.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (question.get().getUser().equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new QuestionDto(question.get()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @PutMapping(value = "/api/question/{id}")
    public ResponseEntity update(HttpServletRequest request, @RequestBody QuestionRequest questionRequest, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Optional<Question> optionalQuestion = questionService.findById(id);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if(!questionRequest.isUpdateValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        if(!optionalQuestion.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        Question question = optionalQuestion.get();

        if (question.getUser().equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {

            if (questionRequest.getTitle() != null && !questionRequest.getTitle().equals("")) {
                question.setTitle(questionRequest.getTitle());
            }

            if (questionRequest.getDescription() != null) {
                question.setDescription(questionRequest.getDescription());
            }

            if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty() && questionRequest.getOptions().size() >= 2) {
                questionService.deleteOptions(question.getOptions());
                question.setOptions(questionRequest.getOptions().stream().map(OptionRequest::toEntity).collect(Collectors.toList()));
                question.setIsMultiple(questionRequest.isMultiple());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new QuestionDto(questionService.update(question)));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @DeleteMapping(value = "/api/question/{id}")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }
        Optional<Question> optionalQuestion = questionService.findById(id);


        if(!optionalQuestion.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }
        if (optionalQuestion.get().getUser().equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {

            questionService.deleteOptions(optionalQuestion.get().getOptions());
            questionService.delete(optionalQuestion.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

}
