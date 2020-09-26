package es.unizar.murcy.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.unizar.murcy.model.Workflow;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("Duplicates")
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String statusBy;

    @Getter
    @Setter
    private String descriptionBy;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String response;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
        workflow.getAuditableWorkflowEntities()
                .stream()
                .findFirst()
                .ifPresent(item -> this.descriptionBy = item.getOwner().getUsername());
        this.response = workflow.getResponse();
        if (workflow.getNextWorkflow() != null) {
            this.nextWorkflow = new WorkflowDto(workflow.getNextWorkflow());
        } else {
            this.nextWorkflow = null;
        }
    }
}
