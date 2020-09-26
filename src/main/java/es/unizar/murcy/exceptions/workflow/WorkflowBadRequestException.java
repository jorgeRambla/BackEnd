package es.unizar.murcy.exceptions.workflow;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "workflow bad request")
public class WorkflowBadRequestException extends RuntimeException {
}
