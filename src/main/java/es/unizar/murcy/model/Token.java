package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "murcy_token")
@AllArgsConstructor
@ToString(callSuper = true)
public class Token extends AuditableEntity {

    public static final int DEFAULT_EXPIRATION_MAX_DAYS = 2;
    public static final long DEFAULT_TOKEN_EXPIRATION_TIME = DEFAULT_EXPIRATION_MAX_DAYS * 24L * 3600L * 1000L;

    @OneToOne
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private Date expirationDate;

    @Getter
    @Setter
    private String tokenValue;

    public Token(User user, String tokenValue, Date expirationDate) {
        super();
        this.user = user;
        this.tokenValue = tokenValue;
        this.expirationDate = expirationDate;
    }

    public Token() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

}
