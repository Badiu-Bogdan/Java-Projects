package repository.DB.exceptions;

public class DBRepositoryPurchaseException extends DBRepositoryException {

    public DBRepositoryPurchaseException(String message) {
        super("DBRepositoryPurchaseException " + message);
    }

    public DBRepositoryPurchaseException(String message, Throwable cause) {
        super("DBRepositoryPurchaseException " + message, cause);
    }

    public DBRepositoryPurchaseException(Throwable cause) {
        super(cause);
    }
}