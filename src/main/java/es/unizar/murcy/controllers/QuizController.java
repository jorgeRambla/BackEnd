package es.unizar.murcy.controllers;


import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.answer.AnswerBadRequestException;
import es.unizar.murcy.exceptions.quiz.QuizBadRequestException;
import es.unizar.murcy.exceptions.quiz.QuizNotFoundException;
import es.unizar.murcy.model.*;
import es.unizar.murcy.model.dto.*;
import es.unizar.murcy.model.request.AnswerRequest;
import es.unizar.murcy.model.request.QuizRequest;
import es.unizar.murcy.service.AnswerService;
import es.unizar.murcy.service.QuestionService;
import es.unizar.murcy.service.QuizService;
import es.unizar.murcy.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class QuizController {

    private final AuthUtilities authUtilities;
    private final QuizService quizService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final WorkflowService workflowService;

    private final Logger logger = LoggerFactory.getLogger(QuizController.class);

    public QuizController(AuthUtilities authUtilities, QuizService quizService, QuestionService questionService,
                          AnswerService answerService, WorkflowService workflowService) {
        this.authUtilities = authUtilities;
        this.quizService = quizService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.workflowService = workflowService;
    }

    @CrossOrigin
    @PostMapping(value = "/api/quiz")
    public ResponseEntity create(HttpServletRequest request, @RequestBody QuizRequest quizRequest) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        if(quizRequest.isCreateValid().equals(Boolean.FALSE)) {
            throw new QuizBadRequestException();
        }

        Quiz createdQuiz = quizRequest.toEntity(questionService);
        createdQuiz.setOwner(requester);

        Workflow workflow;
        if(Boolean.FALSE.equals(quizRequest.getPublish())) {
            workflow = new Workflow();
            workflow.setTitle(Workflow.DRAFT_MESSAGE);
            workflow.setResponse(Workflow.DRAFT_MESSAGE);
            workflow.setDescription(null);
            workflow.setStatusUser(requester);
            workflow.setStatus(Workflow.Status.DRAFT);
            createdQuiz.setClosed(true);
            createdQuiz.setApproved(false);
        } else {
            workflow = new Workflow();
            workflow.setStatusUser(requester);
            workflow.setStatus(Workflow.Status.APPROVED);
            workflow.setTitle("Solicitud publicar quiz");
            workflow.setDescription(null);
            createdQuiz.setClosed(true);
            createdQuiz.setApproved(true);
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
    public ResponseEntity<PageableCollectionDto<QuizDto>> fetchCurrentUserQuizList(
            HttpServletRequest request,
            @RequestParam(value = "all", defaultValue = "true") Boolean fetchAll,
            @RequestParam(value = "published", defaultValue = "false") Boolean published,
            @RequestParam(value = "page", defaultValue = "-1") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            @RequestParam(value = "sortColumn", defaultValue = "createDate") String sortColumn,
            @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
            @RequestParam(value = "query", defaultValue = "") String query) {

        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        return this.fetchUserByIdQuizList(request, requester.getId(), fetchAll, published, page, size, sortColumn, sortType, query);
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/list/{id}")
    public ResponseEntity<PageableCollectionDto<QuizDto>> fetchUserByIdQuizList(
            HttpServletRequest request,
            @PathVariable long id,
            @RequestParam(value = "all", defaultValue = "true") Boolean fetchAll,
            @RequestParam(value = "published", defaultValue = "false") Boolean published,
            @RequestParam(value = "page", defaultValue = "-1") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            @RequestParam(value = "sortColumn", defaultValue = "createDate") String sortColumn,
            @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
            @RequestParam(value = "query", defaultValue = "") String query) {

        logger.info("Handle get request /api/quiz/list/{}: query[{}] all[{}] published[{}] page[{}] size[{}] sortColumn[{}] sortType[{}]",
                id, query, fetchAll, published, page, size, sortColumn, sortType);

        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        User searchedUser;
        if (requester.getId() == id) {
            searchedUser = requester;
        } else {
            searchedUser = this.authUtilities.filterUserAuthorized(requester, id, User.Rol.REVIEWER);
        }

        Page<Quiz> quizPage = quizService.findQuizzesByOwnerId(fetchAll, published, searchedUser, page,
                size, sortColumn, sortType, query);

        Collection<Quiz> quizCollection = quizPage.getContent();
        long totalItems = quizPage.getTotalElements();

        return ResponseEntity.status(HttpStatus.OK).body(
                new PageableCollectionDto<>(quizCollection.stream().map(QuizDto::new).collect(Collectors.toList()), totalItems));
    }

    @CrossOrigin
    @GetMapping(value = "/api/quiz/{id}")
    public ResponseEntity<QuizDto> fetchQuizById(HttpServletRequest request, @PathVariable long id) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, quiz.getOwner(), User.Rol.REVIEWER);

        return ResponseEntity.status(HttpStatus.OK).body(new QuizDto(quiz));
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
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, quiz.getOwner(), User.Rol.REVIEWER);

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
            Workflow workflow;
            if(Boolean.FALSE.equals(quizRequest.getPublish())){
                workflow = Workflow.draftWorkflow(requester, quiz);
            } else {
                workflow = new Workflow();
                workflow.setDescription(null);
                workflow.setStatusUser(null);
                workflow.setStatus(Workflow.Status.APPROVED);
                workflow.setTitle("Solicitud publicar quiz");
                workflow.addAuditableWorkflowEntity(quiz);
                quiz.setClosed(true);
                quiz.setApproved(true);
            }
            workflow = workflowService.create(workflow);

            quiz.getLastWorkflow().setNextWorkflow(workflow);
            workflowService.update(quiz.getLastWorkflow());

            quiz.setLastWorkflow(workflow);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new QuizDto(quizService.update(quiz)));
    }

    @CrossOrigin
    @DeleteMapping(value = "/api/quiz/{id}")
    @SuppressWarnings("Duplicates")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable long id) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, quiz.getOwner(), User.Rol.REVIEWER);

        quizService.delete(quiz);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @CrossOrigin
    @GetMapping("/api/quiz/request/list")
    public ResponseEntity<List<QuizDto>> getQuizRequests(HttpServletRequest request,
                                                 @RequestParam(value = "closed", defaultValue = "false") Boolean isClosed,
                                                 @RequestParam(value = "approved", defaultValue = "false") Boolean isApproved) {
        authUtilities.newUserMiddlewareCheck(request, User.Rol.REVIEWER);

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
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, quiz.getOwner(), User.Rol.REVIEWER);

        return ResponseEntity.status(HttpStatus.OK).body(
                answerService.findAnswersByQuizId(quiz.getId())
                        .stream()
                        .map(AnswerDto::new)
                        .collect(Collectors.toList()));

    }

    @CrossOrigin
    @PostMapping("/api/quiz/{id}/answer")
    public ResponseEntity create(HttpServletRequest request, @PathVariable long id, @RequestBody AnswerRequest answerRequest) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        if (answerRequest.isCreateValid(quiz).equals(Boolean.FALSE)) {
            throw new AnswerBadRequestException();
        }

        Answer answer = answerRequest.toEntity(questionService);
        answer.setQuiz(quiz);
        answer.setUser(requester);

        answerService.create(answer);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}