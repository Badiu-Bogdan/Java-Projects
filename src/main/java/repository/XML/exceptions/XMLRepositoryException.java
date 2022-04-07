package repository.XML.exceptions;

import repository.RepositoryException;

public class XMLRepositoryException extends RepositoryException {

    public XMLRepositoryException(String message) {
        super(message);
    }

    public XMLRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLRepositoryException(Throwable cause) {
        super(cause);
    }
}