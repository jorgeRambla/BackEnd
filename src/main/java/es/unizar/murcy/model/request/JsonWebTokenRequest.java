package es.unizar.murcy.model.request;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonWebTokenRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
}
