package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.editor_request.EditorRequestNotFoundException;
import es.unizar.murcy.model.EditorRequest;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.dto.EditorRequestDto;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.PageableCollectionDto;
import es.unizar.murcy.model.request.EditorRequestRequest;
import es.unizar.murcy.service.EditorRequestService;
import es.unizar.murcy.service.WorkflowService;
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
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class RequestController {

    private final EditorRequestService editorRequestService;
    private final AuthUtilities authUtilities;
    private final WorkflowService workflowService;

    public RequestController(EditorRequestService editorRequestService, AuthUtilities authUtilities,
                             WorkflowService workflowService) {
        this.editorRequestService = editorRequestService;
        this.authUtilities = authUtilities;
        this.workflowService = workflowService;
    }

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
            workflow.setTitle("Application to be an editor");

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
                workflow.setTitle("Application to be an editor");

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
    public ResponseEntity<PageableCollectionDto<EditorRequestDto>> getOpenedEditorRequest(HttpServletRequest request,
                                                                         @RequestParam(value = "closed", defaultValue = "false") Boolean isClosed,
                                                                         @RequestParam(value = "approved", defaultValue = "false") Boolean isApproved,
                                                                         @RequestParam(value = "page", defaultValue = "-1") int page,
                                                                         @RequestParam(value = "size", defaultValue = "50") int size,
                                                                         @RequestParam(value = "sortColumn", defaultValue = "createDate") String sortColumn,
                                                                         @RequestParam(value = "sortType", defaultValue = "desc") String sortType) {
        authUtilities.getUserFromRequest(request, User.Rol.REVIEWER, true);

        Collection<EditorRequest> editorRequestSet;
        long totalItems;
        if (page == -1) {
            editorRequestSet = editorRequestService.findByClosedAndApproved(isClosed, isApproved);
            totalItems = editorRequestSet.size();
        } else {
            Page<EditorRequest> editorRequestPage;
            if (sortType.equalsIgnoreCase("asc")) {
                editorRequestPage = editorRequestService.findByClosedAndApproved(isClosed, isApproved, PageRequest.of(page, size, Sort.by(sortColumn).ascending()));
            } else {
                editorRequestPage = editorRequestService.findByClosedAndApproved(isClosed, isApproved, PageRequest.of(page, size, Sort.by(sortColumn).descending()));
            }
            editorRequestSet = editorRequestPage.getContent();
            totalItems = editorRequestPage.getTotalElements();
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new PageableCollectionDto<>(editorRequestSet.stream().map(EditorRequestDto::new).collect(Collectors.toList()),  totalItems));
    }
}
