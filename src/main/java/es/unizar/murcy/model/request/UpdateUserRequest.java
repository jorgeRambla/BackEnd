package es.unizar.murcy.model.request;

import es.unizar.murcy.model.User;
import lombok.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String fullName;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String[] rol;

    public Set<User.Rol> getRolSet() {
        return Arrays.stream(rol).map(User.Rol::valueOf).collect(Collectors.toSet());
    }
}
