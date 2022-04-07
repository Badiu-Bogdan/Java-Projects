package service.exceptions;

public class ToyServiceException extends ServiceException{

    public ToyServiceException(String message) {
        super("ToyServiceException " + message);
    }

    public ToyServiceException(String message, Throwable cause) {
        super("ToyServiceException " + message, cause);
    }

    public ToyServiceException(Throwable cause) {
        super(cause);
    }
}