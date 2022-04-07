package service.exceptions;

public class StoreServiceException extends ServiceException{

    public StoreServiceException(String message) {
        super("StoreServiceException " + message);
    }

    public StoreServiceException(String message, Throwable cause) {
        super("StoreServiceException " + message, cause);
    }

    public StoreServiceException(Throwable cause) {
        super(cause);
    }
}