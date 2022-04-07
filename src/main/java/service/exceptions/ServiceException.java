package service.exceptions;

public class ServiceException extends RuntimeException{

    public ServiceException(String message) {
        super("ServiceException " + message);
    }

    public ServiceException(String message, Throwable cause) {
        super("ServiceException " + message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
