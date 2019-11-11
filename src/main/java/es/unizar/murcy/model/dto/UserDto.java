package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.User;

public class UserDto {
    private long id;
    private String userName;
    private String email;
    private String fullName;

    private String[] role;

    public UserDto() {
    }

    public UserDto(long id, String userName, String email, String fullName, String[] role) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();

        this.role = user.getRoles().stream().map(User.Rol::name).toArray(String[]::new);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String[] getRole() {
        return role;
    }

    public void setRole(String[] role) {
        this.role = role;
    }
}
