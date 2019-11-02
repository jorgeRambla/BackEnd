package es.unizar.murcy.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "murcy_workflow")
public class Workflow {
    public enum Status {
        APPROVED, PENDING, DENIED, DRAFT, DRAFT_FROM_APPROVED, EXPIRED, INCOMPLETE, SCHEDULED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Status status;

    private Date statusDate;

    private Date createDate;

    private Date modifiedDate;

    private String title;

    private String description;

    private String response;

    @OneToOne
    private Workflow nextWorkflow;

    @ManyToOne
    public User statusUser;

    public Workflow() {
        this.nextWorkflow = null;
        this.createDate = new Date();
        this.modifiedDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Workflow getNextWorkflow() {
        return nextWorkflow;
    }

    public void setNextWorkflow(Workflow nextWorkflow) {
        this.nextWorkflow = nextWorkflow;
    }

    public User getStatusUser() {
        return statusUser;
    }

    public void setStatusUser(User statusUser) {
        this.statusUser = statusUser;
    }
}
