package domain.validators.exceptions;

public class ToyValidatorException extends ValidatorException {
    public ToyValidatorException(String message) {
        super("ToyValidatorException" + message);
    }

    public ToyValidatorException(String message, Throwable cause) {
        super("ToyValidatorException" + message, cause);
    }

    public ToyValidatorException(Throwable cause) {
        super(cause);
    }
}