package es.unizar.murcy.exceptions;

public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String msg;

    public ValidationException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
