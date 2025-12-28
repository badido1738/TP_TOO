package bank.domain.exceptions;

public class UnknownAccountException extends RuntimeException {

    public UnknownAccountException(String message) {
        super(message);
    }

    public UnknownAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
