package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import es.unizar.murcy.model.extendable.jpa.AuditableWorkflowEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "murcy_workflow")
@AllArgsConstructor
@ToString(callSuper = true)
public class Workflow extends AuditableEntity {

    public static final String DRAFT_MESSAGE = "DRAFT";

    @Getter
    @Setter
    private Status status;

    @Getter
    @Setter
    private Date statusDate;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String response;

    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Workflow nextWorkflow;

    @ManyToOne
    @Getter
    @Setter
    private User statusUser;

    @ManyToMany(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Set<AuditableWorkflowEntity> auditableWorkflowEntities;

    public Workflow() {
        super();
        Date now = new Date();
        this.nextWorkflow = null;
        this.status = Status.PENDING;
        this.statusDate = now;
        this.auditableWorkflowEntities = new HashSet<>();
    }

    public enum Status {
        APPROVED, PENDING, DENIED, DRAFT, DRAFT_FROM_APPROVED, EXPIRED, INCOMPLETE, SCHEDULED
    }

    public void addAuditableWorkflowEntity(AuditableWorkflowEntity auditableWorkflowEntity) {
        this.auditableWorkflowEntities.add(auditableWorkflowEntity);
    }

    public static Workflow draftWorkflow(User requester, AuditableWorkflowEntity entity) {
        Workflow workflow = new Workflow();
        workflow.setDescription(null);
        workflow.setStatusUser(requester);
        workflow.setTitle(Workflow.DRAFT_MESSAGE);
        workflow.setResponse(Workflow.DRAFT_MESSAGE);
        entity.setClosed(true);
        entity.setApproved(false);
        if(entity.getLastWorkflow().getStatus().equals(Workflow.Status.APPROVED)){
            workflow.setStatus(Workflow.Status.DRAFT_FROM_APPROVED);
        } else if (entity.getLastWorkflow().getStatus().equals(Workflow.Status.DRAFT_FROM_APPROVED)){
            workflow.setStatus(Workflow.Status.DRAFT_FROM_APPROVED);
        } else {
            workflow.setStatus(Workflow.Status.DRAFT);
        }
        workflow.addAuditableWorkflowEntity(entity);
        return workflow;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    public void clear() {
        this.auditableWorkflowEntities.clear();
    }
}
