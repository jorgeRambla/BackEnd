package es.unizar.murcy.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "murcy_workflow")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private long id;

    @Getter
    @Setter
    private Status status;

    @Getter
    @Setter
    private Date statusDate;

    @Getter
    @Setter
    private Date createDate;

    @Getter
    @Setter
    private Date modifiedDate;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String response;

    @OneToOne
    @Getter
    @Setter
    private Workflow nextWorkflow;


    @ManyToOne
    @Getter
    @Setter
    private User statusUser;

    public Workflow() {
        Date now = new Date();
        this.nextWorkflow = null;
        this.createDate = now;
        this.modifiedDate = now;
        this.status = Status.PENDING;
        this.statusDate = now;
    }

    public enum Status {
        APPROVED, PENDING, DENIED, DRAFT, DRAFT_FROM_APPROVED, EXPIRED, INCOMPLETE, SCHEDULED
    }
}
