package es.unizar.murcy.model;

import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "murcy_user")
@AllArgsConstructor
@ToString(callSuper = true)
public class User extends AuditableEntity {

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

    @OneToMany(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Set<Answer> answers;

    @ElementCollection(targetClass = User.Rol.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "murcy_user_rol")
    @Getter
    @Setter
    private Set<Rol> roles;

    public User() {
        super();
        this.roles = new HashSet<>();
        this.answers = new HashSet<>();
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
        USER, EDITOR, REVIEWER, ADMINISTRATOR
    }

    public void addRol(Rol rol) {
        this.roles.add(rol);
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
