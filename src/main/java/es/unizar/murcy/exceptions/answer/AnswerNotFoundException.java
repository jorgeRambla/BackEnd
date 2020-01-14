package es.unizar.murcy.exceptions.answer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "answer not found")
public class AnswerNotFoundException extends RuntimeException {
}
