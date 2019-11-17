package es.unizar.murcy.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "murcy_question")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private long id;

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
    private Date createDate;

    @Getter
    @Setter
    private Date modifiedDate;

    @Getter
    @Setter
    private Boolean isMultiple;

    @ManyToOne
    @Getter
    @Setter
    private Workflow workflow;

    @ManyToOne
    @Getter
    @Setter
    private Workflow lastWorkflow;

    @Getter
    @Setter
    private Boolean closed;

    @Getter
    @Setter
    private Boolean approved;

    @ManyToMany
    @OrderColumn(name = "INDEX")
    @Getter
    @Setter
    private List<Option> options;

    public Question() {
        this.createDate = new Date();
        this.modifiedDate = new Date();
    }
}