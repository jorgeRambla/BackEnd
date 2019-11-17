package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.EditorRequest;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorRequestDto {

    @Getter @Setter private long id;

    @Getter @Setter private long applicantId;

    @Getter @Setter private String description;

    @Getter @Setter private boolean closed;

    @Getter @Setter private boolean approved;

    @Getter @Setter private WorkflowDto workflow;

    @Getter @Setter private WorkflowDto lastWorkflow;


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
