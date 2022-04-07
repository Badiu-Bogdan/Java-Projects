package repository.XML.exceptions;

public class XMLRepositoryToyException extends XMLRepositoryException {

    public XMLRepositoryToyException(String message) {
        super("XMLRepositoryToyException " + message);
    }

    public XMLRepositoryToyException(String message, Throwable cause) {
        super("XMLRepositoryToyException " + message, cause);
    }

    public XMLRepositoryToyException(Throwable cause) {
        super(cause);
    }
}