package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import es.unizar.murcy.model.extendable.jpa.AuditableWorkflowEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "murcy_workflow")
@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Workflow extends AuditableEntity {

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

    @OneToOne
    @Getter
    @Setter
    private Workflow nextWorkflow;

    @ManyToOne
    @Getter
    @Setter
    private User statusUser;

    @OneToMany
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
}
