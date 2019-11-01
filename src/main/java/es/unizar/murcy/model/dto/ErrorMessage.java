package es.unizar.murcy.model.dto;

import org.springframework.http.HttpStatus;

public class ErrorMessage {

    int status;
    String message;

    public ErrorMessage() {

    }

    public ErrorMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public ErrorMessage(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status.value();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
