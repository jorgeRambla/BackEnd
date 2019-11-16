package es.unizar.murcy.model.request;

import es.unizar.murcy.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class RegisterUserRequest {

    private String username;
    private String password;
    private String email;
    private String fullName;

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
