package es.unizar.murcy.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

public class Token {
    @Id
    @SequenceGenerator(name="token_generator", sequenceName="token_sequence", initialValue = 1)
    @GeneratedValue(generator = "token_generator")
    private Long idToken;

    private Usuario user;

    private Date fecha;

    public Long getIdToken() {
        return idToken;
    }

    public void setIdToken(Long id) {
        this.idToken = id;
    }

    public Usuario getUsername() {
        return user;
    }

    public void setUsername(Usuario username) {
        this.user = username;
    }

    public Date getDate() {
        return fecha;
    }

    public void setDate(Date f) {
        this.fecha = f;
    }


}
