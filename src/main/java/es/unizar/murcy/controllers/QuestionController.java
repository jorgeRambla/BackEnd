package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.user.UserNotFoundException;
import es.unizar.murcy.exceptions.user.UserUnauthorizedException;
import es.unizar.murcy.exceptions.question.QuestionBadRequestException;
import es.unizar.murcy.exceptions.question.QuestionNotFoundException;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.IndividualAnswerDto;
import es.unizar.murcy.model.dto.QuestionDto;
import es.unizar.murcy.model.request.OptionRequest;
import es.unizar.murcy.model.request.QuestionRequest;
import es.unizar.murcy.service.IndividualAnswerService;
import es.unizar.murcy.service.QuestionService;
import es.unizar.murcy.service.UserService;
import es.unizar.murcy.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
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

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private IndividualAnswerService individualAnswerService;

    @CrossOrigin
    @PostMapping(value = "/api/question")
    public ResponseEntity create(HttpServletRequest request, @RequestBody QuestionRequest questionRequest) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (!questionRequest.isValid()) {
            throw new QuestionBadRequestException();
        }

        Question question = questionRequest.toEntity();
        question.setUser(user);

        Workflow workflow;
        if(Boolean.FALSE.equals(questionRequest.getPublish())) {
            workflow = new Workflow();
            workflow.setDescription(null);
            workflow.setStatusUser(user);
            workflow.setTitle(Workflow.DRAFT_MESSAGE);
            workflow.setResponse(Workflow.DRAFT_MESSAGE);
            workflow.setStatus(Workflow.Status.DRAFT);
            question.setClosed(true);
            question.setApproved(false);
        } else {
            workflow = new Workflow();
            workflow.setDescription(null);
            workflow.setStatusUser(null);
            workflow.setTitle("Solicitud publicar pregunta");
        }
        workflow = workflowService.create(workflow);

        question.setWorkflow(workflow);
        question.setLastWorkflow(workflow);

        question = questionService.create(question);

        workflow.addAuditableWorkflowEntity(question);

        workflowService.update(workflow);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping("/api/question/list")
    public ResponseEntity<List<QuestionDto>> fetchCurrentUserQuestionList(HttpServletRequest request) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        return ResponseEntity.status(HttpStatus.OK).body(questionService.findQuestionsByOwner(user).stream().map(QuestionDto::new).collect(Collectors.toList()));
    }

    @CrossOrigin
    @GetMapping("/api/question/list/{id}")
    public ResponseEntity<List<QuestionDto>> fetchCurrentUserQuestionListByOwnerId(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        if (user.getId() == id) {
            return ResponseEntity.status(HttpStatus.OK).body(questionService.findQuestionsByOwnerId(id).stream().map(QuestionDto::new).collect(Collectors.toList()));
        } else if (user.getRoles().contains(User.Rol.REVIEWER)) {
            if (userService.findUserById(id).isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(questionService.findQuestionsByOwner(user).stream().map(QuestionDto::new).collect(Collectors.toList()));
            }
            throw new UserNotFoundException();
        }
        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @GetMapping("/api/question/{id}")
    public ResponseEntity<QuestionDto> fetchQuestionById(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);
        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);

        if (question.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new QuestionDto(question));
        }

        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @PutMapping(value = "/api/question/{id}")
    public ResponseEntity<QuestionDto> update(HttpServletRequest request, @RequestBody QuestionRequest questionRequest, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);

        if(!questionRequest.isUpdateValid()) {
            throw new QuestionBadRequestException();
        }

        if (question.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {

            if (questionRequest.getTitle() != null && !questionRequest.getTitle().equals("")) {
                question.setTitle(questionRequest.getTitle());
            }

            if (questionRequest.getDescription() != null) {
                question.setDescription(questionRequest.getDescription());
            }

            if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
                questionService.deleteOptions(question.getOptions(), false);
                question.setOptions(questionRequest.getOptions().stream().map(OptionRequest::toEntity).collect(Collectors.toList()));
                question.setIsMultiple(questionRequest.isMultiple());
            }

            if(question.isClosed()) {
                Workflow workflow = new Workflow();
                if(Boolean.FALSE.equals(questionRequest.getPublish())){
                    workflow.setStatusUser(user);
                    workflow.setTitle(Workflow.DRAFT_MESSAGE);
                    workflow.setResponse(Workflow.DRAFT_MESSAGE);
                    workflow.setDescription(null);
                    question.setClosed(true);
                    question.setApproved(false);
                    if(question.getLastWorkflow().getStatus().equals(Workflow.Status.APPROVED)) {
                        workflow.setStatus(Workflow.Status.DRAFT_FROM_APPROVED);
                    }
                    else if(question.getLastWorkflow().getStatus().equals(Workflow.Status.DRAFT_FROM_APPROVED)) {
                        workflow.setStatus(Workflow.Status.DRAFT_FROM_APPROVED);
                    }
                    else {
                        workflow.setStatus(Workflow.Status.DRAFT);
                    }
                } else {
                    workflow = new Workflow();
                    workflow.setDescription(null);
                    workflow.setStatusUser(null);
                    workflow.setTitle("Solicitud publicar pregunta");
                    workflow.addAuditableWorkflowEntity(question);
                }
                workflow = workflowService.create(workflow);

                question.getLastWorkflow().setNextWorkflow(workflow);
                workflowService.update(question.getLastWorkflow());

                question.setLastWorkflow(workflow);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new QuestionDto(questionService.update(question)));
        }
        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @DeleteMapping(value = "/api/question/{id}")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);

        if (question.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {
            questionService.deleteOptions(question.getOptions(), false);
            questionService.delete(question);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @GetMapping("/api/question/request/list")
    public ResponseEntity<List<QuestionDto>> getQuestionsRequests(HttpServletRequest request,
                                                 @RequestParam(value = "closed", defaultValue = "false") Boolean isClosed,
                                                 @RequestParam(value = "approved", defaultValue = "false") Boolean isApproved) {
        authUtilities.getUserFromRequest(request, User.Rol.REVIEWER, true);

        Set<Question> editorRequestSet = questionService.findByClosedAndApproved(isClosed, isApproved);

        return ResponseEntity.status(HttpStatus.OK).body(editorRequestSet.stream().map(QuestionDto::new).collect(Collectors.toList()));
    }

    @CrossOrigin
    @GetMapping("/api/question/{id}/answers")
    public ResponseEntity<List<IndividualAnswerDto>> fetchAnswersByQuestionId(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.EDITOR, true);

        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);

        if (question.getUser().equals(user) || user.getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    individualAnswerService.findIndividualAnswersByQuestionId(id)
                            .stream()
                            .map(
                            IndividualAnswerDto::new)
                            .collect(Collectors.toList()));
        }
        throw new UserUnauthorizedException();
    }

}
