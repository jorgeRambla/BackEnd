package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.workflow.WorkflowBadRequestException;
import es.unizar.murcy.exceptions.workflow.WorkflowNotFoundException;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.WorkflowDto;
import es.unizar.murcy.model.request.UpdateWorkflowStatusRequest;
import es.unizar.murcy.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class WorkflowController {

    @Autowired
    WorkflowService workflowService;

    @Autowired
    private AuthUtilities authUtilities;

    @CrossOrigin
    @PutMapping("/api/workflow/{id}/approve")
    public ResponseEntity approveWorkflow(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateWorkflowStatusRequest updateWorkflowStatusRequest) {
        User user = authUtilities.getUserFromRequest(request, User.Rol.REVIEWER, true);

        if(!updateWorkflowStatusRequest.isValid()) {
            throw new WorkflowBadRequestException();
        }

        Workflow workflow = workflowService.approveById(id, user, updateWorkflowStatusRequest.getResponse()).orElseThrow(WorkflowNotFoundException::new);

        return ResponseEntity.status(HttpStatus.CREATED).body(new WorkflowDto(workflow));
    }

    @CrossOrigin
    @PutMapping("/api/workflow/{id}/deny")
    public ResponseEntity denyWorkflow(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateWorkflowStatusRequest updateWorkflowStatusRequest) {

        User user = authUtilities.getUserFromRequest(request, User.Rol.REVIEWER, true);

        if(!updateWorkflowStatusRequest.isValid()) {
            throw new WorkflowBadRequestException();
        }

        Workflow workflow = workflowService.denyById(id, user, updateWorkflowStatusRequest.getResponse()).orElseThrow(WorkflowNotFoundException::new);

        return ResponseEntity.status(HttpStatus.CREATED).body(new WorkflowDto(workflow));
    }
}
