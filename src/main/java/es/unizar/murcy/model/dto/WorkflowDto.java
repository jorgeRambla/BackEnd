package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Workflow;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private Date statusDate;

    @Getter
    @Setter
    private String statusBy;

    @Getter
    @Setter
    private String response;

    @Getter
    @Setter
    private WorkflowDto nextWorkflow;

    public WorkflowDto(Workflow workflow) {
        this.id = workflow.getId();
        this.title = workflow.getTitle();
        this.description = workflow.getDescription();
        this.status = workflow.getStatus().name();
        this.statusDate = workflow.getStatusDate();
        if(workflow.getStatusUser() != null) {
            this.statusBy = workflow.getStatusUser().getUsername();
        }
        this.response = workflow.getResponse();
        if (workflow.getNextWorkflow() != null) {
            this.nextWorkflow = new WorkflowDto(workflow.getNextWorkflow());
        } else {
            this.nextWorkflow = null;
        }
    }
}
