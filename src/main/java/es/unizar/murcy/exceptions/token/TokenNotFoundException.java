package es.unizar.murcy.exceptions.token;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "token not found")
public class TokenNotFoundException extends RuntimeException {
}
