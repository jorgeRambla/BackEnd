package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "murcy_individual_answer")
@AllArgsConstructor
@ToString(callSuper = true)
public class IndividualAnswer extends AuditableEntity {

    @Getter
    @Setter
    private long timeInMillis;

    @Getter
    @Setter
    private Integer points;

    @ManyToOne
    @Getter
    @Setter
    private Question question;

    @ManyToOne
    @Getter
    @Setter
    private Answer answer;

    @Getter
    @Setter
    private List<Option> options;

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