package es.unizar.murcy.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "murcy_workflow")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Workflow extends AuditableEntity{

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
    private Set<AuditableWorkflowEntity> auditableWorkflowEntity;

    public Workflow() {
        super();
        Date now = new Date();
        this.nextWorkflow = null;
        this.status = Status.PENDING;
        this.statusDate = now;
    }

    public enum Status {
        APPROVED, PENDING, DENIED, DRAFT, DRAFT_FROM_APPROVED, EXPIRED, INCOMPLETE, SCHEDULED
    }
}
