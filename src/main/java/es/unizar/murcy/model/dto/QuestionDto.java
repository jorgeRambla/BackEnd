package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.Workflow;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;


public class QuestionDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    private boolean isMultiple;

    private Workflow wF;

    private Workflow lastwF;

    private boolean isPublic;

    private boolean approved;

    private boolean closed;

    private List<Question.Option> options;


    public QuestionDto(){
    }

    public QuestionDto(long id, String t, String d, boolean isMult, boolean isP, boolean approv, boolean closed, List<Question.Option> op) {
        this.id=id;
        this.title=t;
        this.description=d;
        this.options=op;
        this.isMultiple=isMult;
        this.isPublic=isP;
        this.approved=approv;
        this.closed=closed;
        this.wF=null;
        this.lastwF=null;
    }
    public QuestionDto(Question q){
        this.id=q.getId();
        this.title=q.getTitle();
        this.description=q.getDescription();
        this.options=q.getOptions();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public List<Question.Option> getOptions() {
        return this.options;
    }

    public void setOptions(List<Question.Option> a) {
        this.options = a;
    }

    public boolean getIsMultipleValue(){
        return this.isMultiple;
    }

    public void setIsMultipleValue(boolean isM){
        this.isMultiple=isM;
    }

    public Workflow getWorkflow(){
        return this.wF;
    }

    public void setWorkflow(Workflow workF){
        this.wF=workF;
    }

    public Workflow getLastWorkflow(){
        return this.lastwF;
    }

    public void setLastWorkflow(Workflow lastworkF){
        this.lastwF=lastworkF;
    }

    public boolean getIsPublicValue(){
        return this.isPublic;
    }

    public void setIsPublicValue(boolean isP){
        this.isPublic=isP;
    }


}
