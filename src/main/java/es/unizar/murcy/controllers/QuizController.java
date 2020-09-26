package es.unizar.murcy.controllers;


import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.answer.AnswerBadRequestException;
import es.unizar.murcy.exceptions.quiz.QuizBadRequestException;
import es.unizar.murcy.exceptions.quiz.QuizNotFoundException;
import es.unizar.murcy.exceptions.user.UserUnauthorizedException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
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
        if(quizRequest.getPublish().equals(Boolean.FALSE)) {
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
            workflow.setStatus(Workflow.Status.PENDING);
            workflow.setTitle("Request to publish quiz");
            createdQuiz.setClosed(false);
            createdQuiz.setApproved(false);
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
    public ResponseEntity<SimplifiedQuizDto> fetchQuizByIdPublic(HttpServletRequest request , @PathVariable long id) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.USER);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        try {
            authUtilities.filterUserAuthorized(requester, quiz.getOwner(), User.Rol.REVIEWER);
        } catch (UserUnauthorizedException uae) {
         if (!quiz.isClosed() || !quiz.isApproved()) {
             throw new QuizNotFoundException();
         }
        }

        if (!quiz.canBePlayed().equals(Boolean.TRUE)) {
            throw new QuizNotFoundException();
        }

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

        if (quizRequest.getPublish() == null || quizRequest.getPublish().equals(Boolean.FALSE)) {
            if(quizRequest.getQuestionsIds() == null) {
                quiz.setQuestions(new ArrayList<>());
            }
        } else {
            if ((quizRequest.getQuestionsIds() != null) && !quizRequest.getQuestionsIds().isEmpty()) {
                quiz.setQuestions(questionService.findByIdsCollection(quizRequest.getQuestionsIds()));
            }
        }

        quiz.setQuestionsOrdered(quizRequest.getOrdered());

        if(quiz.isClosed()) {
            Workflow workflow;
            if(quizRequest.getPublish().equals(Boolean.FALSE)){
                workflow = Workflow.draftWorkflow(requester, quiz);
            } else {
                workflow = new Workflow();
                workflow.setStatus(Workflow.Status.PENDING);
                workflow.setTitle("Request to publish quiz");
                workflow.addAuditableWorkflowEntity(quiz);
                quiz.setClosed(false);
                quiz.setApproved(false);
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
    public ResponseEntity delete(HttpServletRequest request, @PathVariable long id) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, quiz.getOwner(), User.Rol.REVIEWER);

        quizService.delete(quiz);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @CrossOrigin
    @GetMapping("/api/quiz/request/list")
    public ResponseEntity<PageableCollectionDto<QuizDto>> getQuizRequests(
            HttpServletRequest request,
            @RequestParam(value = "all", defaultValue = "false") Boolean fetchAll,
            @RequestParam(value = "closed", defaultValue = "false") Boolean isClosed,
            @RequestParam(value = "approved", defaultValue = "false") Boolean isApproved,
            @RequestParam(value = "page", defaultValue = "-1") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            @RequestParam(value = "sortColumn", defaultValue = "createDate") String sortColumn,
            @RequestParam(value = "sortType", defaultValue = "desc") String sortType) {

        logger.info("Handle get request /api/quiz/request/list: all[{}] closed[{}] approved[{}] page[{}] size[{}] sortColumn[{}] sortType[{}]",
                fetchAll, isClosed, isApproved, page, size, sortColumn, sortType);

        authUtilities.newUserMiddlewareCheck(request, User.Rol.REVIEWER);

        Page<Quiz> quizPage = quizService.findByClosedAndApproved(
                fetchAll, isClosed, isApproved, page, size, sortColumn, sortType);

        Collection<Quiz> quizSet = quizPage.getContent();
        long totalItems = quizPage.getTotalElements();

        return ResponseEntity.status(HttpStatus.OK).body(
                new PageableCollectionDto<>(
                        quizSet
                                .stream()
                                .map(QuizDto::new)
                                .collect(Collectors.toList()),
                        totalItems));
    }

    @CrossOrigin
    @GetMapping("/api/quiz/search")
    public ResponseEntity<PageableCollectionDto<SimplifiedQuizDto>> searchQuiz(HttpServletRequest request,
                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "50") int size,
                                            @RequestParam(value = "sortColumn", defaultValue = "title") String sortColumn,
                                            @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
                                            @RequestParam(value = "query", defaultValue = "") String query) {

        // For search currently we don't have to track user.
        Page<Quiz> quizPage = quizService.searchQuizzes(query, page, size, sortColumn, sortType);

        Collection<Quiz> quizCollection = quizPage.getContent();
        long totalItems = quizPage.getTotalElements();

        return ResponseEntity.status(HttpStatus.OK).body(
                new PageableCollectionDto<>(quizCollection.stream().map(SimplifiedQuizDto::buildWithoutQuestions).collect(Collectors.toList()), totalItems));
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
                        .map(item -> new AnswerDto(item, questionService))
                        .collect(Collectors.toList()));

    }

    @CrossOrigin
    @PostMapping("/api/quiz/{id}/answer")
    public ResponseEntity<Long> create(HttpServletRequest request, @PathVariable long id, @RequestBody AnswerRequest answerRequest) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Quiz quiz = quizService.findById(id).orElseThrow(QuizNotFoundException::new);

        if (answerRequest.isCreateValid(quiz).equals(Boolean.FALSE)) {
            throw new AnswerBadRequestException();
        }

        Answer answer = answerRequest.toEntity(questionService);
        answer.setQuiz(quiz);
        answer.setUser(requester);

        answerService.create(answer);

        return ResponseEntity.status(HttpStatus.CREATED).body(answer.getTotalPoints());
    }
}