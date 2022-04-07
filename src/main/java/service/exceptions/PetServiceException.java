package service.exceptions;

public class PetServiceException extends ServiceException{

    public PetServiceException(String message) {
        super("PetServiceException " + message);
    }

    public PetServiceException(String message, Throwable cause) {
        super("PetServiceException " + message, cause);
    }

    public PetServiceException(Throwable cause) {
        super(cause);
    }
}