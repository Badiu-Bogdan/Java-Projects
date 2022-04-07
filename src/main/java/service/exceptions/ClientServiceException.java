package service.exceptions;

public class ClientServiceException extends ServiceException{

    public ClientServiceException(String message) {
        super("ClientServiceException " + message);
    }

    public ClientServiceException(String message, Throwable cause) {
        super("ClientServiceException " + message, cause);
    }

    public ClientServiceException(Throwable cause) {
        super(cause);
    }
}