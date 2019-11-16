package es.unizar.murcy.model.request;

import es.unizar.murcy.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String[] rol;



    public Set<User.Rol> getRolSet() {
        return Arrays.stream(rol).map(User.Rol::valueOf).collect(Collectors.toSet());
    }
}
