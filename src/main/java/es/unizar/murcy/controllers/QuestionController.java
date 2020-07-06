package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.question.QuestionBadRequestException;
import es.unizar.murcy.exceptions.question.QuestionNotFoundException;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.IndividualAnswerDto;
import es.unizar.murcy.model.dto.PageableCollectionDto;
import es.unizar.murcy.model.dto.QuestionDto;
import es.unizar.murcy.model.request.OptionRequest;
import es.unizar.murcy.model.request.QuestionRequest;
import es.unizar.murcy.service.IndividualAnswerService;
import es.unizar.murcy.service.QuestionService;
import es.unizar.murcy.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class QuestionController {

    private final AuthUtilities authUtilities;
    private final QuestionService questionService;
    private final WorkflowService workflowService;
    private final IndividualAnswerService individualAnswerService;

    private final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    public QuestionController(AuthUtilities authUtilities, QuestionService questionService,
                              WorkflowService workflowService,
                              IndividualAnswerService individualAnswerService) {
        this.authUtilities = authUtilities;
        this.questionService = questionService;
        this.workflowService = workflowService;
        this.individualAnswerService = individualAnswerService;
    }

    @CrossOrigin
    @PostMapping(value = "/api/question")
    public ResponseEntity create(HttpServletRequest request, @RequestBody QuestionRequest questionRequest) {
        this.logger.info("Handle request POST /api/question: {}", questionRequest);
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);
        
        if (!questionRequest.isValid()) {
            throw new QuestionBadRequestException();
        }

        Question question = questionRequest.toEntity();
        question.setOwner(requester);

        question = questionService.create(question);

        Workflow workflow = new Workflow();
        if(Boolean.FALSE.equals(questionRequest.getPublish())) {
            workflow.setDescription(null);
            workflow.setStatusUser(requester);
            workflow.setTitle(Workflow.DRAFT_MESSAGE);
            workflow.setResponse(Workflow.DRAFT_MESSAGE);
            workflow.setStatus(Workflow.Status.DRAFT);
            question.setClosed(true);
            question.setApproved(false);
        } else {
            workflow.setDescription(null);
            workflow.setStatusUser(requester);
            workflow.setTitle("Publish question request");
            workflow.setResponse("Automatic approved question");
            workflow.setStatus(Workflow.Status.APPROVED);
            question.setClosed(true);
            question.setApproved(true);
        }

        question.setWorkflow(workflow);
        question.setLastWorkflow(workflow);

        questionService.create(question);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping("/api/question/list")
    public ResponseEntity<PageableCollectionDto<QuestionDto>> fetchCurrentUserQuestionList(
            HttpServletRequest request,
            @RequestParam(value = "all", defaultValue = "true") Boolean fetchAll,
            @RequestParam(value = "published", defaultValue = "false") Boolean published,
            @RequestParam(value = "page", defaultValue = "-1") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            @RequestParam(value = "sortColumn", defaultValue = "createDate") String sortColumn,
            @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
            @RequestParam(value = "query", defaultValue = "") String query) {

        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        return this.fetchCurrentUserQuestionListByOwnerId(request, requester.getId(), fetchAll, published, page,
                size, sortColumn, sortType, query);
    }

    @CrossOrigin
    @GetMapping("/api/question/list/{id}")
    public ResponseEntity<PageableCollectionDto<QuestionDto>> fetchCurrentUserQuestionListByOwnerId(
            HttpServletRequest request,
            @PathVariable long id,
            @RequestParam(value = "all", defaultValue = "true") Boolean fetchAll,
            @RequestParam(value = "published", defaultValue = "true") Boolean published,
            @RequestParam(value = "page", defaultValue = "-1") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            @RequestParam(value = "sortColumn", defaultValue = "createDate") String sortColumn,
            @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
            @RequestParam(value = "query", defaultValue = "") String query) {

        logger.info("Handle get request /api/question/list/{}: query[{}] all[{}] published[{}] page[{}] size[{}] sortColumn[{}] sortType[{}]",
                id, query, fetchAll, published, page, size, sortColumn, sortType);

        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        User searchedUser = this.authUtilities.filterUserAuthorized(requester, id, User.Rol.REVIEWER);

        Page<Question> questionPage = questionService.findQuestionsByOwnerId(fetchAll, published, searchedUser, page,
                size, sortColumn, sortType, query);

        Collection<Question> questionCollection = questionPage.getContent();
        long totalItems = questionPage.getTotalElements();

        return ResponseEntity.status(HttpStatus.OK).body(
                new PageableCollectionDto<>(questionCollection.stream().map(QuestionDto::new).collect(Collectors.toList()), totalItems));
    }

    @CrossOrigin
    @GetMapping("/api/question/{id}")
    public ResponseEntity<QuestionDto> fetchQuestionById(HttpServletRequest request, @PathVariable long id) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);
        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, question.getOwner(), User.Rol.REVIEWER);

        return ResponseEntity.status(HttpStatus.OK).body(new QuestionDto(question));
    }

    @CrossOrigin
    @PutMapping(value = "/api/question/{id}")
    public ResponseEntity<QuestionDto> update(HttpServletRequest request, @RequestBody QuestionRequest questionRequest, @PathVariable long id) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);
        
        if(!questionRequest.isUpdateValid()) {
            throw new QuestionBadRequestException();
        }

        authUtilities.filterUserAuthorized(requester, question.getOwner(), User.Rol.REVIEWER);

        if (questionRequest.getTitle() != null && !questionRequest.getTitle().equals("")) {
            question.setTitle(questionRequest.getTitle());
        }

        if (questionRequest.getDescription() != null) {
            question.setDescription(questionRequest.getDescription());
        }

        if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
            questionService.deleteOptions(question);
            question.setOptions(questionRequest.getOptions().stream().map(OptionRequest::toEntity).collect(Collectors.toList()));
            question.setIsMultiple(questionRequest.isMultiple());
        }

        if(question.isClosed()) {
            Workflow workflow = new Workflow();
            if(Boolean.FALSE.equals(questionRequest.getPublish())){
                workflow = Workflow.draftWorkflow(requester, question);
            } else {
                workflow.setDescription(null);
                workflow.setStatusUser(requester);
                workflow.setTitle("Publish question request");
                workflow.setResponse("Automatic approved question");
                workflow.setStatus(Workflow.Status.APPROVED);
                question.setClosed(true);
                question.setApproved(true);
                workflow.addAuditableWorkflowEntity(question);
            }
            workflow = workflowService.create(workflow);

            question.getLastWorkflow().setNextWorkflow(workflow);
            workflowService.update(question.getLastWorkflow());

            question.setLastWorkflow(workflow);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new QuestionDto(questionService.update(question)));
    }

    @CrossOrigin
    @DeleteMapping(value = "/api/question/{id}")
    public ResponseEntity<String> delete(HttpServletRequest request, @PathVariable long id) {
        this.logger.info("Handle DELETE /api/question/{}", id);
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, question.getOwner(), User.Rol.REVIEWER);

        questionService.delete(question);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpStatus.ACCEPTED.toString());
    }

    @CrossOrigin
    @GetMapping("/api/question/request/list")
    public ResponseEntity<List<QuestionDto>> getQuestionsRequests(HttpServletRequest request,
                                                 @RequestParam(value = "closed", defaultValue = "false") Boolean isClosed,
                                                 @RequestParam(value = "approved", defaultValue = "false") Boolean isApproved) {
        authUtilities.newUserMiddlewareCheck(request, User.Rol.REVIEWER);

        Set<Question> editorRequestSet = questionService.findByClosedAndApproved(isClosed, isApproved);

        return ResponseEntity.status(HttpStatus.OK).body(editorRequestSet.stream().map(QuestionDto::new).collect(Collectors.toList()));
    }

    @CrossOrigin
    @GetMapping("/api/question/{id}/answers")
    public ResponseEntity<List<IndividualAnswerDto>> fetchAnswersByQuestionId(HttpServletRequest request, @PathVariable long id) {
        User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.EDITOR);

        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);

        authUtilities.filterUserAuthorized(requester, question.getOwner(), User.Rol.REVIEWER);

        return ResponseEntity.status(HttpStatus.OK).body(
                individualAnswerService.findIndividualAnswersByQuestionId(id)
                        .stream()
                        .map(
                        IndividualAnswerDto::new)
                        .collect(Collectors.toList()));
    }

}
