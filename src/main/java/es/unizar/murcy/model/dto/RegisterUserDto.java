package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.User;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class RegisterUserDto {

    private String username;
    private String password;
    private String email;
    private String fullName;

    public RegisterUserDto() {
    }

    public RegisterUserDto(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public User toEntity() {
        User user = new User();
        user.setFullName(this.fullName);
        user.setPassword(this.password);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setConfirmed(false);

        Set<User.Rol> roles = new HashSet<>();
        roles.add(User.Rol.USER);

        user.setRoles(roles);
        return user;
    }

    public Boolean isComplete() {
        return password != null && fullName != null && username != null && email != null;
    }
}
