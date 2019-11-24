package es.unizar.murcy.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "murcy_question")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Question extends AuditableWorkflowEntity{

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