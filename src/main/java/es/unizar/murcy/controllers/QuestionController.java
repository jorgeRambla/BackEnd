package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.QuestionDto;
import es.unizar.murcy.model.request.OptionRequest;
import es.unizar.murcy.model.request.QuestionRequest;
import es.unizar.murcy.service.QuestionService;
import es.unizar.murcy.service.UserService;
import es.unizar.murcy.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
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

        Workflow workflow= null;
        if(Boolean.FALSE.equals(questionRequest.getPublish())) {
            workflow = new Workflow();
            workflow.setDescription(null);
            workflow.setStatusUser(user.get());
            workflow.setTitle("Borrador");
            workflow.setResponse("Borrador");
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

            if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
                questionService.deleteOptions(question.getOptions(), false);
                question.setOptions(questionRequest.getOptions().stream().map(OptionRequest::toEntity).collect(Collectors.toList()));
                question.setIsMultiple(questionRequest.isMultiple());
            }

            if(question.isClosed()) {
                Workflow workflow = new Workflow();
                if(Boolean.FALSE.equals(questionRequest.getPublish())){
                    workflow.setDescription(null);
                    workflow.setStatusUser(user.get());
                    workflow.setTitle("Borrador");
                    workflow.setResponse("Borrador");
                    question.setClosed(true);
                    question.setApproved(false);
                    if(question.getLastWorkflow().getStatus().equals(Workflow.Status.APPROVED)){
                        workflow.setStatus(Workflow.Status.DRAFT_FROM_APPROVED);
                    } else {
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

            questionService.deleteOptions(optionalQuestion.get().getOptions(), false);
            questionService.delete(optionalQuestion.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @GetMapping("/api/question/request/list")
    public ResponseEntity getQuestionsRequests(HttpServletRequest request,
                                                 @RequestParam(value = "closed", defaultValue = "false") Boolean isClosed,
                                                 @RequestParam(value = "approved", defaultValue = "false") Boolean isApproved) {
        Optional<User> user = authUtilities.getUserFromRequest(request, User.Rol.REVIEWER, true);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Set<Question> editorRequestSet = questionService.findByClosedAndApproved(isClosed, isApproved);

        return ResponseEntity.status(HttpStatus.OK).body(editorRequestSet.stream().map(QuestionDto::new).collect(Collectors.toList()));
    }

}
