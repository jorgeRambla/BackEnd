package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.workflow.WorkflowBadRequestException;
import es.unizar.murcy.exceptions.workflow.WorkflowNotFoundException;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.WorkflowDto;
import es.unizar.murcy.model.request.UpdateWorkflowStatusRequest;
import es.unizar.murcy.service.WorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class WorkflowController {

    private final WorkflowService workflowService;
    private final AuthUtilities authUtilities;

    public WorkflowController(WorkflowService workflowService, AuthUtilities authUtilities) {
        this.workflowService = workflowService;
        this.authUtilities = authUtilities;
    }

    @CrossOrigin
    @PutMapping("/api/workflow/{id}/approve")
    public ResponseEntity<WorkflowDto> approveWorkflow(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateWorkflowStatusRequest updateWorkflowStatusRequest) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.REVIEWER);

        if(!updateWorkflowStatusRequest.isValid()) {
            throw new WorkflowBadRequestException();
        }

        Workflow workflow = workflowService.approveById(id, requester, updateWorkflowStatusRequest.getResponse()).orElseThrow(WorkflowNotFoundException::new);

        return ResponseEntity.status(HttpStatus.CREATED).body(new WorkflowDto(workflow));
    }

    @CrossOrigin
    @PutMapping("/api/workflow/{id}/deny")
    public ResponseEntity<WorkflowDto> denyWorkflow(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateWorkflowStatusRequest updateWorkflowStatusRequest) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.REVIEWER);

        if(!updateWorkflowStatusRequest.isValid()) {
            throw new WorkflowBadRequestException();
        }

        Workflow workflow = workflowService.denyById(id, requester, updateWorkflowStatusRequest.getResponse()).orElseThrow(WorkflowNotFoundException::new);

        return ResponseEntity.status(HttpStatus.CREATED).body(new WorkflowDto(workflow));
    }
}
