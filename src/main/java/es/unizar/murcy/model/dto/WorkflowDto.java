package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Workflow;

import java.util.Date;

public class WorkflowDto {
    private long id;
    private String title;
    private String description;
    private String status;
    private Date statusDate;
    private String response;

    private WorkflowDto nextWorkflow;

    public WorkflowDto() {
    }

    public WorkflowDto(Workflow workflow) {
        this.id = workflow.getId();
        this.title = workflow.getTitle();
        this.description = workflow.getDescription();
        this.status = workflow.getStatus().name();
        this.statusDate = workflow.getStatusDate();
        this.response = workflow.getResponse();
        if(workflow.getNextWorkflow() != null) {
            this.nextWorkflow = new WorkflowDto(workflow.getNextWorkflow());
        } else {
            this.nextWorkflow = null;
        }
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public WorkflowDto getNextWorkflow() {
        return nextWorkflow;
    }

    public void setNextWorkflow(WorkflowDto nextWorkflow) {
        this.nextWorkflow = nextWorkflow;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
