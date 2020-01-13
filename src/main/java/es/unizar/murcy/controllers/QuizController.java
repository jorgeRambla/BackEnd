package es.unizar.murcy.controllers;


import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.QuizDto;
import es.unizar.murcy.model.dto.SimplifiedQuizDto;
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

//TODO: controller for getting all individual answers for one question GET /api/quiz/{id}/answers -> Lista de respuestas

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
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if(quizRequest.isCreateValid().equals(Boolean.FALSE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        Quiz createdQuiz = quizRequest.toEntity(questionService);
        createdQuiz.setUser(user.get());

        Workflow workflow= null;
        if(Boolean.FALSE.equals(quizRequest.getPublish())) {
            workflow = new Workflow();
            workflow.setTitle(Workflow.DRAFT_MESSAGE);
            workflow.setResponse(Workflow.DRAFT_MESSAGE);
            workflow.setDescription(null);
            workflow.setStatusUser(user.get());
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

            if(quiz.isClosed()) {
                Workflow workflow = new Workflow();
                if(Boolean.FALSE.equals(quizRequest.getPublish())){
                    workflow.setDescription(null);
                    workflow.setStatusUser(user.get());
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

    @CrossOrigin
    @GetMapping("/api/quiz/request/list")
    public ResponseEntity getQuizRequests(HttpServletRequest request,
                                                 @RequestParam(value = "closed", defaultValue = "false") Boolean isClosed,
                                                 @RequestParam(value = "approved", defaultValue = "false") Boolean isApproved) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.REVIEWER, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Set<Quiz> editorRequestSet = quizService.findByClosedAndApproved(isClosed, isApproved);

        return ResponseEntity.status(HttpStatus.OK).body(
                editorRequestSet.stream()
                        .map(QuizDto::new)
                        .collect(Collectors.toList())
        );
    }

    @CrossOrigin
    @GetMapping("/api/quiz/search")
    public ResponseEntity searchQuiz(HttpServletRequest request,
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

//        if (answerList==null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
//        } <- ESTA CONDICION NUNCA VA A SER, SI ESO DEVUELVE LISTA VACIA, SOLO SE DEVUELVE 404 SI EL ID NO EXISTE

        if (user.equals(user.get()) || user.get().getRoles().contains(User.Rol.REVIEWER)) { // <- Optional<User> equals Optinal<User>::get ?¿¿?¿?¿ No querras decir quiz user owner equals Optional<User>::get ?¿?
            return ResponseEntity.status(HttpStatus.OK).body(answerList);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }
}