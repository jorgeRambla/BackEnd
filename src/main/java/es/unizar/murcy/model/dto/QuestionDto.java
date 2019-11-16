package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Option;
import es.unizar.murcy.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private long id;

    private String title;

    private String userName;

    private String description;

    private boolean isMultiple;

    private WorkflowDto workflow;

    private WorkflowDto lastWorkflow;

    private boolean approved;

    private boolean closed;

    private List<Option> options;

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.userName = question.getUser().getUsername();
        this.isMultiple = question.getMultiple();
        this.description = question.getDescription();
        this.workflow = new WorkflowDto(question.getWorkflow());
        this.lastWorkflow = new WorkflowDto(question.getLastWorkflow());
        this.options = question.getOptions();
        this.approved = question.getApproved();
        this.closed = question.getClosed();
    }
}
