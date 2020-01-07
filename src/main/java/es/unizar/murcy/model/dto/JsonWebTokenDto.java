package es.unizar.murcy.model.dto;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class JsonWebTokenDto implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    @Getter
    @Setter
    private @NonNull String jsonWebToken;
}