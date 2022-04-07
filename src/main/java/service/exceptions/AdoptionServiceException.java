package service.exceptions;

public class AdoptionServiceException extends ServiceException{

    public AdoptionServiceException(String message) {
        super("AdoptionServiceException " + message);
    }

    public AdoptionServiceException(String message, Throwable cause) {
        super("AdoptionServiceException " + message, cause);
    }

    public AdoptionServiceException(Throwable cause) {
        super(cause);
    }
}