package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.WorkflowDto;
import es.unizar.murcy.model.request.UpdateWorkflowStatusRequest;
import es.unizar.murcy.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if(!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if(!user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if(!updateWorkflowStatusRequest.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        Optional<Workflow> workflow = workflowService.approveById(id, user.get(), updateWorkflowStatusRequest.getResponse());

        if(workflow.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new WorkflowDto(workflow.get()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
    }

    @CrossOrigin
    @PutMapping("/api/workflow/{id}/deny")
    public ResponseEntity denyWorkflow(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateWorkflowStatusRequest updateWorkflowStatusRequest) {

        Optional<User> user = authUtilities.getUserFromRequest(request);

        if(!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if(!user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        if(!updateWorkflowStatusRequest.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        Optional<Workflow> workflow = workflowService.denyById(id, user.get(), updateWorkflowStatusRequest.getResponse());

        if(workflow.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new WorkflowDto(workflow.get()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
    }
}
