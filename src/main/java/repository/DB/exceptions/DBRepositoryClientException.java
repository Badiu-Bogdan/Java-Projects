package repository.DB.exceptions;

public class DBRepositoryClientException extends DBRepositoryException {

    public DBRepositoryClientException(String message) {
        super("DBRepositoryClientException " + message);
    }

    public DBRepositoryClientException(String message, Throwable cause) {
        super("DBRepositoryClientException " + message, cause);
    }

    public DBRepositoryClientException(Throwable cause) {
        super(cause);
    }
}