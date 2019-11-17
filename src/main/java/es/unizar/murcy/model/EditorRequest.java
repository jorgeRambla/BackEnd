package es.unizar.murcy.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "murcy_editor_request")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EditorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private long id;

    @ManyToOne
    @Getter
    @Setter
    private User applicant;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private Date createDate;

    @Getter
    @Setter
    private Date modifiedDate;

    @ManyToOne
    @Getter
    @Setter
    private Workflow workflow;

    @Getter
    @Setter
    private boolean closed;

    @Getter
    @Setter
    private boolean approved;

    @ManyToOne
    @Getter
    @Setter
    private Workflow lastWorkflow;

    public EditorRequest() {
        this.createDate = new Date();
        this.modifiedDate = new Date();
        this.closed = false;
        this.approved = false;
    }
}
