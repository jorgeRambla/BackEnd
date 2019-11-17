package es.unizar.murcy.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "murcy_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Token {

    public static final int DEFAULT_EXPIRATION_MAX_DAYS = 2;
    public static final long DEFAULT_TOKEN_EXPIRATION_TIME = DEFAULT_EXPIRATION_MAX_DAYS * 24L * 3600L * 1000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private long id;

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
        this.user = user;
        this.tokenValue = tokenValue;
        this.expirationDate = expirationDate;
    }
}
