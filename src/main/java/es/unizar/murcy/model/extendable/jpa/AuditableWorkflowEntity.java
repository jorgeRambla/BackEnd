package es.unizar.murcy.model.extendable.jpa;

import es.unizar.murcy.model.Workflow;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "murcy_auditableWorkflow")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@ToString(callSuper = true)
public class AuditableWorkflowEntity extends AuditableEntity{

    @ManyToOne
    @Getter
    @Setter
    private Workflow workflow;

    @ManyToOne
    @Getter
    @Setter
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

}
