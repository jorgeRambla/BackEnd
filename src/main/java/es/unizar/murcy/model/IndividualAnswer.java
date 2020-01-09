package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableWorkflowEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "murcy_individual_answer")
@AllArgsConstructor
@ToString(callSuper = true)
public class IndividualAnswer extends AuditableWorkflowEntity {

    @Getter
    @Setter
    private String answerText;

    @Getter
    @Setter
    private Integer resolutionTime;

    @Getter
    @Setter
    private Integer points;

    @ManyToOne
    @Getter
    @Setter
    private Question question;

    //Desde la general a las individuales y de la individual a una general.
    @ManyToMany(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Set<Answer> answers;

    public IndividualAnswer() {
        super();
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