package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Quiz;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("Duplicates")
public class QuizDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String ownerUserName;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private long ownerId;

    @Getter
    @Setter
    private String description;

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
    private List<SimplifiedQuestionDto> questions;

    public QuizDto(Quiz quiz){
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.description = quiz.getDescription();
        this.ownerUserName = quiz.getUser().getUsername();
        this.ownerId = quiz.getUser().getId();
        this.questions = quiz.getQuestions().stream().map(SimplifiedQuestionDto::new).collect(Collectors.toList());
        if(quiz.getWorkflow() != null) {
            this.workflow = new WorkflowDto(quiz.getWorkflow());
        }
        if(quiz.getLastWorkflow() != null) {
            this.lastWorkflow = new WorkflowDto(quiz.getLastWorkflow());
        }
        this.approved = quiz.isApproved();
        this.closed = quiz.isClosed();
    }
}
