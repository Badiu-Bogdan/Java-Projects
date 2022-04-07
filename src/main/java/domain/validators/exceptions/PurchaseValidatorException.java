package domain.validators.exceptions;

public class PurchaseValidatorException extends ValidatorException {
    public PurchaseValidatorException(String message) {
        super("PurchaseValidatorException " + message);
    }

    public PurchaseValidatorException(String message, Throwable cause) {
        super("PurchaseValidatorException " + message, cause);
    }

    public PurchaseValidatorException(Throwable cause) {
        super(cause);
    }
}