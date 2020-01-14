package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.editor_request.EditorRequestNotFoundException;
import es.unizar.murcy.model.EditorRequest;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.EditorRequestDto;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.request.EditorRequestRequest;
import es.unizar.murcy.service.EditorRequestService;
import es.unizar.murcy.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class RequestController {


    @Autowired
    private EditorRequestService editorRequestService;

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private WorkflowService workflowService;

    @CrossOrigin
    @GetMapping("/api/request/editor")
    public ResponseEntity<EditorRequestDto> getCurrentUserEditorRequest(HttpServletRequest request) {
        User user = authUtilities.getUserFromRequest(request);

        EditorRequest editorRequest = editorRequestService.findEditorRequestByApplicant(user).orElseThrow(EditorRequestNotFoundException::new);

        return ResponseEntity.ok().body(new EditorRequestDto(editorRequest));
    }

    @CrossOrigin
    @PutMapping("/api/request/editor")
    public ResponseEntity putCurrentUserEditorRequest(HttpServletRequest request, @RequestBody EditorRequestRequest editorRequestRequest) {
        User user = authUtilities.getUserFromRequest(request);

        EditorRequest editorRequest = editorRequestService.findEditorRequestByApplicant(user).orElseThrow(EditorRequestNotFoundException::new);

        String description = (editorRequestRequest.getDescription() == null) ? "" : editorRequestRequest.getDescription();

        editorRequest.setDescription(description);

        Workflow finalWorkFlow = editorRequest.getLastWorkflow();
        finalWorkFlow.setDescription(description);

        workflowService.update(finalWorkFlow);

        editorRequestService.update(editorRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @PostMapping("/api/request/editor")
    public ResponseEntity createCurrentUserEditorRequest(HttpServletRequest request, @RequestBody EditorRequestRequest editorRequestRequest) {
        User user = authUtilities.getUserFromRequest(request);

        Optional<EditorRequest> editorRequest = editorRequestService.findEditorRequestByApplicant(user);

        String description = (editorRequestRequest.getDescription() == null) ? "" : editorRequestRequest.getDescription();

        if (!editorRequest.isPresent()) {
            EditorRequest finalEditorRequest = new EditorRequest();
            finalEditorRequest.setApplicant(user);
            finalEditorRequest.setDescription(description);

            Workflow workflow = new Workflow();
            workflow.setDescription(description);
            workflow.setStatusUser(null);
            workflow.setTitle("Solicitud para ser editor");

            workflow = workflowService.create(workflow);

            finalEditorRequest.setWorkflow(workflow);
            finalEditorRequest.setLastWorkflow(workflow);

            EditorRequest updatedEditorRequest = editorRequestService.create(finalEditorRequest);
            workflow.addAuditableWorkflowEntity(updatedEditorRequest);

            workflowService.update(workflow);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            if (editorRequest.get().isClosed() && !editorRequest.get().isApproved()) {
                EditorRequest finalEditorRequest = editorRequest.get();

                Workflow workflow = new Workflow();
                workflow.setDescription(description);
                workflow.setStatusUser(null);
                workflow.setTitle("[RE] Solicitud para ser editor");

                workflow = workflowService.create(workflow);

                Workflow lastWorkflow = finalEditorRequest.getLastWorkflow();

                lastWorkflow.setNextWorkflow(workflow);
                workflowService.update(lastWorkflow);

                finalEditorRequest.setLastWorkflow(workflow);
                finalEditorRequest.setClosed(false);

                EditorRequest updatedEditorRequest = editorRequestService.update(finalEditorRequest);
                workflow.addAuditableWorkflowEntity(updatedEditorRequest);

                workflowService.update(workflow);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else if (editorRequest.get().isApproved()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessageDto(HttpStatus.CONFLICT, "Request is approved"));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessageDto(HttpStatus.CONFLICT, "There is one pending request"));
            }
        }
    }

    @CrossOrigin
    @GetMapping("/api/request/editor/list")
    public ResponseEntity<List<EditorRequestDto>> getOpenedEditorRequest(HttpServletRequest request,
                                                                         @RequestParam(value = "closed", defaultValue = "false") Boolean isClosed,
                                                                         @RequestParam(value = "approved", defaultValue = "false") Boolean isApproved) {
        authUtilities.getUserFromRequest(request, User.Rol.REVIEWER, true);

        Set<EditorRequest> editorRequestSet = editorRequestService.findByClosedAndApproved(isClosed, isApproved);

        return ResponseEntity.status(HttpStatus.OK).body(editorRequestSet.stream().map(EditorRequestDto::new).collect(Collectors.toList()));
    }
}
