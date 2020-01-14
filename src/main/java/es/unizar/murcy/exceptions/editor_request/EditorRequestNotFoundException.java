package es.unizar.murcy.exceptions.editor_request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "editor request not found")
public class EditorRequestNotFoundException extends RuntimeException {
}
