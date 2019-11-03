package es.unizar.murcy.model.request;

import es.unizar.murcy.model.User;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateUserRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String rol[];

    public UpdateUserRequest(String username) {
        this.username = username;
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

    public String[] getRol() {
        return rol;
    }

    public void setRol(String[] rol) {
        this.rol = rol;
    }

    public Set<User.Rol> getRolSet() {
        return Arrays.stream(rol).map(User.Rol::valueOf).collect(Collectors.toSet());
    }
}
