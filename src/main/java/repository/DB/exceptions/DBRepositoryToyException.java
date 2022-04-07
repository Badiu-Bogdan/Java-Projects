package repository.DB.exceptions;

public class DBRepositoryToyException extends DBRepositoryException {

    public DBRepositoryToyException(String message) {
        super("DBRepositoryToyException " + message);
    }

    public DBRepositoryToyException(String message, Throwable cause) {
        super("DBRepositoryToyException " + message, cause);
    }

    public DBRepositoryToyException(Throwable cause) {
        super(cause);
    }
}