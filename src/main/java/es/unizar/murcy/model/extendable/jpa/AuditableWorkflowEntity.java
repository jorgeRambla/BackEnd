package es.unizar.murcy.model.extendable.jpa;

import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import lombok.*;
import org.hibernate.jdbc.Work;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "murcy_auditableWorkflow")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@ToString(callSuper = true)
public class AuditableWorkflowEntity extends AuditableEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @Getter
    private Workflow workflow;

    @ManyToOne(cascade = CascadeType.ALL)
    @Getter
    private Workflow lastWorkflow;

    @Getter
    @Setter
    private boolean closed;

    @Getter
    @Setter
    private boolean approved;

    @Getter
    @Setter
    private String classname;

    @ManyToOne
    @Getter
    @Setter
    private User owner;

    public AuditableWorkflowEntity() {
        super();
        this.approved = false;
        this.closed = false;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
        workflow.clear();
        this.workflow.addAuditableWorkflowEntity(this);
    }

    public void setLastWorkflow(Workflow workflow) {
        this.lastWorkflow = workflow;
    }

}
