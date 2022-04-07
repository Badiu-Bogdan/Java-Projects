package repository.DB.exceptions;

import repository.RepositoryException;

public class DBRepositoryException extends RepositoryException {

    public DBRepositoryException(String message) {
        super(message);
    }

    public DBRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBRepositoryException(Throwable cause) {
        super(cause);
    }
}