package es.unizar.murcy.exceptions.quiz;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "quiz bad request")
public class QuizBadRequestException extends RuntimeException {
}
