package es.unizar.murcy.exceptions.question;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "question bad request")
public class QuestionBadRequestException extends RuntimeException {
}
