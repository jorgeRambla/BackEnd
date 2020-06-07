package es.unizar.murcy.exceptions.user.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "already exists other user with that email")
public class UserBadRequestEmailAlreadyExistsException extends RuntimeException {
}
