package es.unizar.murcy.service;

import es.unizar.murcy.model.*;
import es.unizar.murcy.repository.AuditableWorkflowEntityRepository;
import es.unizar.murcy.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    WorkflowRepository workflowRepository;

    @Autowired
    AuditableWorkflowEntityRepository auditableWorkflowEntityRepository;

    @Autowired
    UserService userService;

    public Workflow create(Workflow workflow) {
        return  workflowRepository.save(workflow);
    }

    public Workflow update(Workflow workflow) {
        workflow.setModifiedDate(new Date());
        return workflowRepository.save(workflow);
    }

    @Transactional
    public Optional<Workflow> approveById(long id, User approvedUser, String response) {
        return manageWorkflowActions(id, approvedUser, response);
    }

    private Optional<Workflow> manageWorkflowActions(long id, User approvedUser, String response) {
        Optional<Workflow> optionalWorkflow = workflowRepository.findById(id);
        if (optionalWorkflow.isPresent()) {
            Workflow workflow = optionalWorkflow.get();
            workflow.setStatusUser(approvedUser);
            workflow.setStatusDate(new Date());
            workflow.setStatus(Workflow.Status.APPROVED);
            workflow.setResponse(response);

            for (AuditableWorkflowEntity auditableWorkflowEntity : workflow.getAuditableWorkflowEntities()) {
                auditableWorkflowEntity.setApproved(true);
                auditableWorkflowEntity.setClosed(true);
                manageWorkflowActions(auditableWorkflowEntity, Workflow.Status.APPROVED);
            }

            auditableWorkflowEntityRepository.saveAll(workflow.getAuditableWorkflowEntities());

            return Optional.of(update(workflow));
        } else {
            return optionalWorkflow;
        }
    }

    @Transactional
    public Optional<Workflow> denyById(long id, User approvedUser, String response) {
        Optional<Workflow> optionalWorkflow = workflowRepository.findById(id);
        if(optionalWorkflow.isPresent()) {
            Workflow workflow = optionalWorkflow.get();
            workflow.setStatusUser(approvedUser);
            workflow.setStatusDate(new Date());
            workflow.setStatus(Workflow.Status.DENIED);
            workflow.setResponse(response);

            for(AuditableWorkflowEntity auditableWorkflowEntity : workflow.getAuditableWorkflowEntities()) {
                auditableWorkflowEntity.setApproved(false);
                auditableWorkflowEntity.setClosed(true);
                manageWorkflowActions(auditableWorkflowEntity, Workflow.Status.DENIED);
            }

            auditableWorkflowEntityRepository.saveAll(workflow.getAuditableWorkflowEntities());

            return Optional.of(update(workflow));
        } else {
            return optionalWorkflow;
        }
    }

    private void manageWorkflowActions(AuditableWorkflowEntity auditableWorkflowEntity, Workflow.Status status) {
        if(auditableWorkflowEntity.getClassname().equals(EditorRequest.class.getName())) {
            manageWorkflowActionsEditorRequest(auditableWorkflowEntity, status);
        }
    }

    private void manageWorkflowActionsEditorRequest(AuditableWorkflowEntity auditableWorkflowEntity, Workflow.Status status) {
        if(status.equals(Workflow.Status.APPROVED)) {
            EditorRequest editorRequest = (EditorRequest) auditableWorkflowEntity;
            User applicant = editorRequest.getApplicant();
            applicant.addRol(User.Rol.EDITOR);
            userService.update(applicant);
        }
    }
}
