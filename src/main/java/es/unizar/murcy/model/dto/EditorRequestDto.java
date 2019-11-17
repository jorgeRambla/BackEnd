package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.EditorRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorRequestDto {
    private long id;
    private long applicantId;
    private String description;
    private boolean closed;
    private boolean approved;
    private WorkflowDto workflow;
    private WorkflowDto lastWorkflow;


    public EditorRequestDto(EditorRequest  editorRequest) {
        this.id = editorRequest.getId();
        this.applicantId = editorRequest.getApplicant().getId();
        this.description = editorRequest.getDescription();
        this.closed = editorRequest.isClosed();
        this.approved = editorRequest.isApproved();
        this.workflow = new WorkflowDto(editorRequest.getWorkflow());
        this.lastWorkflow = new WorkflowDto(editorRequest.getLastWorkflow());
    }
}
