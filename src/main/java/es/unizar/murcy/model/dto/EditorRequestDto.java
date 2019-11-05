package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.EditorRequest;

public class EditorRequestDto {
    private long id;
    private long applicantId;
    private String description;
    private boolean closed;
    private boolean approved;
    private WorkflowDto workflow;
    private WorkflowDto lastWorkflow;

    public EditorRequestDto() {
    }

    public EditorRequestDto(EditorRequest  editorRequest) {
        this.id = editorRequest.getId();
        this.applicantId = editorRequest.getApplicant().getId();
        this.description = editorRequest.getDescription();
        this.closed = editorRequest.isClosed();
        this.approved = editorRequest.isApproved();
        this.workflow = new WorkflowDto(editorRequest.getWorkflow());
        this.lastWorkflow = new WorkflowDto(editorRequest.getLastWorkflow());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public WorkflowDto getWorkflow() {
        return workflow;
    }

    public void setWorkflow(WorkflowDto workflow) {
        this.workflow = workflow;
    }

    public long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(long applicantId) {
        this.applicantId = applicantId;
    }
}
