package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableWorkflowEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "murcy_editor_request")
@Data
@AllArgsConstructor
public class EditorRequest extends AuditableWorkflowEntity {

    @ManyToOne
    @Getter
    @Setter
    private User applicant;

    @Getter
    @Setter
    private String description;

    public EditorRequest() {
        super();
        super.setClassname(this.getClass().getName());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

}
