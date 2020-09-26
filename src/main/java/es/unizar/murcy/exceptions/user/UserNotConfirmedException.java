package es.unizar.murcy.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "user not confirmed")
public class UserNotConfirmedException extends RuntimeException {
}
