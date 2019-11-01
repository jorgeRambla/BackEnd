package es.unizar.murcy.model.dto;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jsonWebToken;
    public JwtResponse(String jsonWebToken) {
        this.jsonWebToken = jsonWebToken;
    }
    public String getToken() {
        return this.jsonWebToken;
    }
}