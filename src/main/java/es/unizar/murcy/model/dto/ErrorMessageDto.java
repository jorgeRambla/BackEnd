package es.unizar.murcy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageDto {

    private int status;
    private String message;

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
}
