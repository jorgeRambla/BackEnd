package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Column(length = 5096)
    @Getter
    @Setter
    private String options;

    public IndividualAnswer() {
        super();
    }

    public List<Long> getOptionsIds() {
        return Arrays.stream(this.options.split(",")).map(Long::valueOf).collect(Collectors.toList());
    }

    public void setOptionsIds(List<Long> optionsIds) {
        this.options = optionsIds.stream().map(String::valueOf).collect(Collectors.joining(","));
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