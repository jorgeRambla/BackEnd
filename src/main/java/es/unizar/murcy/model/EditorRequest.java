package es.unizar.murcy.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "murcy_editor_request")
public class EditorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User applicant;

    private String description;

    private Date createDate;

    private Date modifiedDate;

    @ManyToOne
    private Workflow workflow;

    @ManyToOne
    private Workflow lastWorkflow;

    public EditorRequest() {
        this.createDate = new Date();
        this.modifiedDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Workflow getLastWorkflow() {
        return lastWorkflow;
    }

    public void setLastWorkflow(Workflow lastWorkflow) {
        this.lastWorkflow = lastWorkflow;
    }
}
