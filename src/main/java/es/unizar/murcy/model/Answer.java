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
@Table(name = "murcy_answer")
@AllArgsConstructor
@ToString(callSuper = true)
public class Answer extends AuditableWorkflowEntity {

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private Integer totalResolutionTime;

    @Getter
    @Setter
    private Integer totalPoints;

    @ManyToOne
    @Getter
    @Setter
    private Quiz quiz;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderColumn(name = "INDEX")
    @Getter
    @Setter
    private List<IndividualAnswer> individualAnswers;

    public Answer() {
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
