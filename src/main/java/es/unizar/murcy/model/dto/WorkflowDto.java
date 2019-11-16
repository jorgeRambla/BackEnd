package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Workflow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class WorkflowDto {
    private long id;
    private String title;
    private String description;
    private String status;
    private Date statusDate;
    private String response;

    private WorkflowDto nextWorkflow;

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
}
