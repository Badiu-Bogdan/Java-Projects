package domain.validators.exceptions;

public class AdoptionValidatorException extends ValidatorException {
    public AdoptionValidatorException(String message) {
        super("AdoptionValidatorException " + message);
    }

    public AdoptionValidatorException(String message, Throwable cause) {
        super("AdoptionValidatorException " + message, cause);
    }

    public AdoptionValidatorException(Throwable cause) {
        super(cause);
    }
}