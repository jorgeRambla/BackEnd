package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableWorkflowEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "murcy_question")
@AllArgsConstructor
@ToString(callSuper = true)
public class Question extends AuditableWorkflowEntity {

    public static final int MIN_OPTIONS = 2;
    public static final int MAX_OPTIONS = 4;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private Boolean isMultiple;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {ALL})
    @OrderColumn(name = "INDEX")
    @Getter
    @Setter
    private List<Option> options;

    @ManyToMany(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Set<Quiz> quizzes;

    public Question() {
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