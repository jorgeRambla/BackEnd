package es.unizar.murcy.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "murcy_question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    private Date createDate;

    private Date modifiedDate;

    @ElementCollection(targetClass=Question.Option.class)
    @OrderBy("number")
    private List<Option> options;

    public static class Option{
        int number;
        String optionTitle;
        boolean correct;
    }

    public Question(){
        this.createDate = new Date();
        this.modifiedDate = new Date();
        this.options = new ArrayList<Option>();
    }

    public Question(long id, String t, String d, List<Option> a) {
        this.id=id;
        this.title=t;
        this.description=d;
        this.options=a;
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

    public List<Option> getOptions() {
        return this.options;
    }

    public void setOptions(List<Option> a) {
        this.options = a;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}