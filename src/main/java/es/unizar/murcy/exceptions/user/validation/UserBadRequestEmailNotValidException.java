package es.unizar.murcy.exceptions.user.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "email is not valid")
public class UserBadRequestEmailNotValidException extends RuntimeException {
}
