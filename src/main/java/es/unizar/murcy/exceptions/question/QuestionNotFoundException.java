package es.unizar.murcy.exceptions.question;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "question not found")
public class QuestionNotFoundException extends RuntimeException {
}
