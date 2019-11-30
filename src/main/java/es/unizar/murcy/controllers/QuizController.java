package es.unizar.murcy.controllers;


import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.QuizDto;
import es.unizar.murcy.model.request.QuizRequest;
import es.unizar.murcy.service.QuestionService;
import es.unizar.murcy.service.QuizService;
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
public class QuizController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping(value = "/api/quiz")
    public ResponseEntity create(HttpServletRequest request, @RequestBody QuizRequest quizRequest) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if(quizRequest.isCreateValid().equals(Boolean.FALSE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        Quiz createdQuiz = quizRequest.toEntity(questionService);
        createdQuiz.setUser(user.get());
        quizService.create(createdQuiz);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/list")
    public ResponseEntity fetchCurrentUserQuizList(HttpServletRequest request) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        return ResponseEntity.status(HttpStatus.OK).body(quizService.findQuizzesByOwnerId(user.get()).stream().map(QuizDto::new).collect(Collectors.toList()));
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/list/{id}")
    public ResponseEntity fetchUserByIdQuizList(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<User> optionalUser = userService.findUserById(id);

        if(!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if(user.get().getId() == id || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(quizService.findQuizzesByOwnerId(id).stream().map(QuizDto::new).collect(Collectors.toList()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/{id}")
    @SuppressWarnings("Duplicates")
    public ResponseEntity fetchQuizById(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<Quiz> optionalQuiz = quizService.findById(id);

        if (!optionalQuiz.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (optionalQuiz.get().getUser().equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new QuizDto(optionalQuiz.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @PutMapping(value = "/api/quiz/{id}")
    @SuppressWarnings("Duplicates")
    public ResponseEntity update(HttpServletRequest request, @RequestBody QuizRequest quizRequest, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<Quiz> optionalQuiz = quizService.findById(id);

        if (!optionalQuiz.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (optionalQuiz.get().getUser().equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {

            Quiz quiz = optionalQuiz.get();
            if (quizRequest.getTitle() != null && !quizRequest.getTitle().equals("")) {
                quiz.setTitle(quizRequest.getTitle());
            }

            if (quizRequest.getDescription() != null) {
                quiz.setDescription(quizRequest.getDescription());
            }

            if (quizRequest.getQuestionIds() != null && !quizRequest.getQuestionIds().isEmpty()) {
                quiz.setQuestions(quizRequest.getQuestionIds()
                        .stream()
                        .map(questionService::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new QuizDto(quizService.update(quiz)));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @DeleteMapping(value = "/api/quiz/{id}")
    @SuppressWarnings("Duplicates")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<Quiz> optionalQuiz = quizService.findById(id);

        if (!optionalQuiz.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if (optionalQuiz.get().getUser().equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) {

            quizService.delete(optionalQuiz.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }
}