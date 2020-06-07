package es.unizar.murcy.model.dto;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("Duplicates")
public class JsonWebTokenDto implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    @Getter
    @Setter
    private @NonNull String jsonWebToken;

    @Getter
    @Setter
    private long expirationTime;
}