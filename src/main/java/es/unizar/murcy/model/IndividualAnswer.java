package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "murcy_individual_answer")
@AllArgsConstructor
@ToString(callSuper = true)
public class IndividualAnswer extends AuditableEntity {

    @ManyToOne
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private long timeInMillis;

    @Getter
    @Setter
    private long points;

    @ManyToOne
    @Getter
    @Setter
    private Question question;

    @ManyToOne
    @Getter
    @Setter
    private Answer answer;

    @OneToMany
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