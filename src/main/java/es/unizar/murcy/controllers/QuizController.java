package es.unizar.murcy.controllers;


import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.answer.AnswerBadRequestException;
import es.unizar.murcy.exceptions.user.UserNotFoundException;
import es.unizar.murcy.exceptions.user.UserUnauthorizedException;
import es.unizar.murcy.exceptions.quiz.QuizBadRequestException;
import es.unizar.murcy.exceptions.quiz.QuizNotFoundException;
import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.AnswerDto;
import es.unizar.murcy.model.dto.QuizDto;
import es.unizar.murcy.model.dto.SimplifiedQuizDto;
import es.unizar.murcy.model.request.AnswerRequest;
import es.unizar.murcy.model.request.QuizRequest;
import es.unizar.murcy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
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
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkflowService workflowService;

    @CrossOrigin
    @PostMapping(value = "/api/quiz")
    public ResponseEntity create(HttpServletRequest request, @RequestBody QuizRequest quizRequest) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if(quizRequest.isCreateValid().equals(Boolean.FALSE)) {
            throw new QuizBadRequestException();
        }

        Quiz createdQuiz = quizRequest.toEntity(questionService);
        createdQuiz.setUser(user);

        Workflow workflow;
        if(Boolean.FALSE.equals(quizRequest.getPublish())) {
            workflow = new Workflow();
            workflow.setTitle(Workflow.DRAFT_MESSAGE);
            workflow.setResponse(Workflow.DRAFT_MESSAGE);
            workflow.setDescription(null);
            workflow.setStatusUser(user);
            workflow.setStatus(Workflow.Status.DRAFT);
            createdQuiz.setClosed(true);
            createdQuiz.setApproved(false);
        } else {
            workflow = new Workflow();
            workflow.setStatusUser(null);
            workflow.setTitle("Solicitud publicar quiz");
            workflow.setDescription(null);
        }

        workflow = workflowService.create(workflow);

        createdQuiz.setLastWorkflow(workflow);
        createdQuiz.setWorkflow(workflow);

        createdQuiz = quizService.create(createdQuiz);

        workflow.addAuditableWorkflowEntity(createdQuiz);

        workflowService.update(workflow);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/list")
    public ResponseEntity<List<QuizDto>> fetchCurrentUserQuizList(HttpServletRequest request) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        return ResponseEntity.status(HttpStatus.OK).body(quizService.findQuizzesByOwnerId(user).stream().map(QuizDto::new).collect(Collectors.toList()));
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/list/{id}")
    public ResponseEntity<List<QuizDto>> fetchUserByIdQuizList(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if(user.getId() == id) {
            return ResponseEntity.status(HttpStatus.OK).body(quizService.findQuizzesByOwnerId(id).stream().map(QuizDto::new).collect(Collectors.toList()));
        } else if (user.getRoles().contains(User.Rol.REVIEWER)) {
            if(userService.findUserById(id).isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(quizService.findQuizzesByOwnerId(id).stream().map(QuizDto::new).collect(Collectors.toList()));
            }
            throw new UserNotFoundException();
        }
       throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/{id}")
    public ResponseEntity<QuizDto> fetchQuizById(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        if (quiz.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new QuizDto(quiz));
        }
        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/{id}/public")
    public ResponseEntity<SimplifiedQuizDto> fetchQuizByIdPublic(@PathVariable long id) {
        Quiz quiz = quizService.findByPublishAndId(id).orElseThrow(QuizNotFoundException::new);

        return ResponseEntity.status(HttpStatus.OK).body(new SimplifiedQuizDto(quiz));
    }

    @CrossOrigin
    @PutMapping(value = "/api/quiz/{id}")
    public ResponseEntity<QuizDto> update(HttpServletRequest request, @RequestBody QuizRequest quizRequest, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);


        if (quiz.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {

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

            if(quiz.isClosed()) {
                Workflow workflow = new Workflow();
                if(Boolean.FALSE.equals(quizRequest.getPublish())){
                    workflow.setDescription(null);
                    workflow.setStatusUser(user);
                    workflow.setTitle(Workflow.DRAFT_MESSAGE);
                    workflow.setResponse(Workflow.DRAFT_MESSAGE);
                    quiz.setClosed(true);
                    quiz.setApproved(false);
                    if(quiz.getLastWorkflow().getStatus().equals(Workflow.Status.APPROVED)){
                        workflow.setStatus(Workflow.Status.DRAFT_FROM_APPROVED);
                    } else if (quiz.getLastWorkflow().getStatus().equals(Workflow.Status.DRAFT_FROM_APPROVED)){
                        workflow.setStatus(Workflow.Status.DRAFT_FROM_APPROVED);
                    } else {
                            workflow.setStatus(Workflow.Status.DRAFT);
                    }
                } else {
                    workflow = new Workflow();
                    workflow.setDescription(null);
                    workflow.setStatusUser(null);
                    workflow.setTitle("Solicitud publicar quiz");
                    workflow.addAuditableWorkflowEntity(quiz);
                }
                workflow = workflowService.create(workflow);

                quiz.getLastWorkflow().setNextWorkflow(workflow);
                workflowService.update(quiz.getLastWorkflow());

                quiz.setLastWorkflow(workflow);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new QuizDto(quizService.update(quiz)));
        }
        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @DeleteMapping(value = "/api/quiz/{id}")
    @SuppressWarnings("Duplicates")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        if (quiz.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {

            quizService.delete(quiz);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @GetMapping("/api/quiz/request/list")
    public ResponseEntity<List<QuizDto>> getQuizRequests(HttpServletRequest request,
                                                 @RequestParam(value = "closed", defaultValue = "false") Boolean isClosed,
                                                 @RequestParam(value = "approved", defaultValue = "false") Boolean isApproved) {
        authUtilities.getUserFromRequest(request, User.Rol.REVIEWER, true);

        Set<Quiz> editorRequestSet = quizService.findByClosedAndApproved(isClosed, isApproved);

        return ResponseEntity.status(HttpStatus.OK).body(
                editorRequestSet.stream()
                        .map(QuizDto::new)
                        .collect(Collectors.toList())
        );
    }

    @CrossOrigin
    @GetMapping("/api/quiz/search")
    public ResponseEntity<List<SimplifiedQuizDto>> searchQuiz(HttpServletRequest request,
                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "50") int size,
                                            @RequestParam(value = "sortColumn", defaultValue = "title") String sortColumn,
                                            @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
                                            @RequestParam(value = "query", defaultValue = "") String query) {

        // For search currently we don't have to track user.

        List<Quiz> resultSet;
        if(sortType.equalsIgnoreCase("asc")) {
            resultSet = quizService.searchQuizzes(query, PageRequest.of(page, size, Sort.by(sortColumn).ascending()));
        } else {
            resultSet = quizService.searchQuizzes(query, PageRequest.of(page, size, Sort.by(sortColumn).descending()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                resultSet.stream()
                        .map(SimplifiedQuizDto::new)
                        .collect(Collectors.toList())
        );
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/{id}/answers")
    public ResponseEntity<List<AnswerDto>> fetchAnswersByQuizId(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        if (quiz.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    answerService.findAnswersByQuizId(quiz.getId())
                            .stream()
                            .map(AnswerDto::new)
                            .collect(Collectors.toList()));
        }
        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @PostMapping("/api/quiz/{id}/answer")
    public ResponseEntity create(HttpServletRequest request, @PathVariable long id, @RequestBody AnswerRequest answerRequest) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        if (answerRequest.isCreateValid(quiz).equals(Boolean.FALSE)) {
            throw new AnswerBadRequestException();
        }

        Answer answer = answerRequest.toEntity(questionService);
        answer.setQuiz(quiz);
        answer.setUser(user);

        answerService.create(answer);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}