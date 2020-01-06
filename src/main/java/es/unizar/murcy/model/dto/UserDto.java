package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.User;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Getter
    @Setter
    private long id;
    @Getter
    @Setter
    private String userName;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String fullName;

    @Getter
    @Setter
    private String[] role;

    public UserDto(User user) {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();

        this.role = user.getRoles().stream().map(User.Rol::name).toArray(String[]::new);
    }
}
