package es.unizar.murcy.model;

import javax.persistence.*;

@Entity
public class HelloWorld {

    @Id
    @SequenceGenerator(name="helloWorld_generator", sequenceName="helloWorld_sequence", initialValue = 1)
    @GeneratedValue(generator = "helloWorld_generator")
    private long id;

    @Column(name = "name", nullable = false, length = 150)
    private String message;

    public HelloWorld(String message) {
        this.message = message;
    }

    public HelloWorld() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
