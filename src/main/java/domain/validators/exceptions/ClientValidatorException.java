package domain.validators.exceptions;

public class ClientValidatorException extends ValidatorException {
    public ClientValidatorException(String message) {
        super("ClientValidatorException " + message);
    }

    public ClientValidatorException(String message, Throwable cause) {
        super("ClientValidatorException " + message, cause);
    }

    public ClientValidatorException(Throwable cause) {
        super(cause);
    }
}