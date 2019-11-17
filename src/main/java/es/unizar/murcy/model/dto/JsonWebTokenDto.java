package es.unizar.murcy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonWebTokenDto implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private @NonNull String jsonWebToken;
}