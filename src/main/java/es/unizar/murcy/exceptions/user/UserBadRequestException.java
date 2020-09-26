package es.unizar.murcy.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "user bad request")
public class UserBadRequestException extends RuntimeException {
}
