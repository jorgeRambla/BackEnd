package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class UserDto {
    private long id;
    private String userName;
    private String email;
    private String fullName;

    private String[] role;

    public UserDto(User user) {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();

        this.role = user.getRoles().stream().map(User.Rol::name).toArray(String[]::new);
    }
}
