package es.unizar.murcy.model.dto;

import org.springframework.http.HttpStatus;

public class ErrorMessageDto {

    private int status;
    private String message;

    public ErrorMessageDto() {

    }

    public ErrorMessageDto(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public ErrorMessageDto(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public ErrorMessageDto(HttpStatus status) {
        this.status = status.value();
        switch(this.status) {
            case 400:
                this.message = "Bad Request";
                break;
            case 401:
                this.message = "User not authorized";
                break;
            default:
                this.message = "";
                break;
        }
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
