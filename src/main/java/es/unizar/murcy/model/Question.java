package es.unizar.murcy.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "murcy_question")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Question extends AuditableWorkflowEntity{

    public static final int MIN_OPTIONS = 2;
    public static final int MAX_OPTIONS = 4;

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

    @Getter
    @Setter
    private Boolean isMultiple;

    @ManyToMany
    @OrderColumn(name = "INDEX")
    @Getter
    @Setter
    private List<Option> options;

    public Question() {
        super();
        super.setClassname(this.getClass().getName());
    }


}