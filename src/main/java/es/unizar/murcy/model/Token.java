package es.unizar.murcy.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "token")
public class Token {

    public static final int DEFAULT_EXPIRATION_MAX_DAYS = 2;
    public static final long DEFAULT_TOKEN_EXPIRATION_TIME = DEFAULT_EXPIRATION_MAX_DAYS * 24 * 3600 * 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private User user;

    private Date expirationDate;

    private String tokenValue;

    public Token() {
    }

    public Token(User user, String tokenValue, Date expirationDate) {
        this.user = user;
        this.tokenValue = tokenValue;
        this.expirationDate = expirationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUsername() {
        return user;
    }

    public void setUsername(User username) {
        this.user = username;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
