package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Option;
import es.unizar.murcy.model.Question;

import java.util.List;


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


    public QuestionDto(){
    }

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

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setWorkflow(WorkflowDto workflow) {
        this.workflow = workflow;
    }

    public void setLastWorkflow(WorkflowDto lastWorkflow) {
        this.lastWorkflow = lastWorkflow;
    }

    public WorkflowDto getWorkflow() {
        return workflow;
    }

    public WorkflowDto getLastWorkflow() {
        return lastWorkflow;
    }
}
