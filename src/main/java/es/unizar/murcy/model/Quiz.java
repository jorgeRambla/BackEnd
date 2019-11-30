package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableWorkflowEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "murcy_quiz")
@AllArgsConstructor
@ToString(callSuper = true)
public class Quiz extends AuditableWorkflowEntity {

    @Getter
    @Setter
    private String title;

    @ManyToOne
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderColumn(name = "INDEX")
    @Getter
    @Setter
    private List<Question> questions;

    public Quiz() {
        super();
        super.setClassname(this.getClass().getName());
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
