package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Question;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("Duplicates")
public class QuestionDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String ownerUserName;

    @Getter
    @Setter
    private long ownerId;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private boolean isMultiple;

    @Getter
    @Setter
    private WorkflowDto workflow;

    @Getter
    @Setter
    private WorkflowDto lastWorkflow;

    @Getter
    @Setter
    private boolean approved;

    @Getter
    @Setter
    private boolean closed;

    @Getter
    @Setter
    private List<OptionDto> options;

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.ownerUserName = question.getOwner().getUsername();
        this.ownerId = question.getOwner().getId();
        this.isMultiple = question.getIsMultiple();
        this.description = question.getDescription();
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        if(question.getWorkflow() != null) {
            this.workflow = new WorkflowDto(question.getWorkflow());
        }
        if(question.getLastWorkflow() != null) {
            this.lastWorkflow = new WorkflowDto(question.getLastWorkflow());
        }
        this.approved = question.isApproved();
        this.closed = question.isClosed();
    }
}
