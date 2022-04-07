package domain.validators.exceptions;

public class PetValidatorException extends ValidatorException {
    public PetValidatorException(String message) {
        super("PetValidatorException " + message);
    }

    public PetValidatorException(String message, Throwable cause) {
        super("PetValidatorException " + message, cause);
    }

    public PetValidatorException(Throwable cause) {
        super(cause);
    }
}