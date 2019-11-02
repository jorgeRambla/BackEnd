package es.unizar.murcy.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "murcy_user")
public class User {

    public enum Rol {
        USER, EDITOR, REVIEWER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    private String password;

    private String fullName;

    private String email;

    private String lastIp;

    private Boolean confirmed;

    private Date createDate;

    private Date modifiedDate;

    @ElementCollection(targetClass=User.Rol.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="murcy_user_rol")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public void addRol(Rol rol) {
        this.roles.add(rol);
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
