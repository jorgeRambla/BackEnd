package es.unizar.murcy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "user not found")
public class UserUnauthorizedException extends RuntimeException {
}
