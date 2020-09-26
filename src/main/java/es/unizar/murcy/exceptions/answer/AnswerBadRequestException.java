package es.unizar.murcy.exceptions.answer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "answer bad request")
public class AnswerBadRequestException extends RuntimeException {
}
