package es.unizar.murcy.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "murcy_user")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private long id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String fullName;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String lastIp;

    @Getter
    @Setter
    private Boolean confirmed;

    @Getter
    @Setter
    private Date createDate;

    @Getter
    @Setter
    private Date modifiedDate;

    @ElementCollection(targetClass = User.Rol.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "murcy_user_rol")
    @Getter
    @Setter
    private Set<Rol> roles;

    public User() {
        this.roles = new HashSet<>();
        this.createDate = new Date();
        this.modifiedDate = new Date();
    }

    public User(String username, String password, String email, String fullName) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.confirmed = false;
    }

    public enum Rol {
        USER, EDITOR, REVIEWER
    }

    public void addRol(Rol rol) {
        this.roles.add(rol);
    }
}
