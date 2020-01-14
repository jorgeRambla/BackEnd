package es.unizar.murcy.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "user is not allowed to access")
public class UserForbiddenException extends RuntimeException {
}
