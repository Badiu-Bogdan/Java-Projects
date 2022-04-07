package repository.XML.exceptions;

public class XMLRepositoryClientException extends XMLRepositoryException {

    public XMLRepositoryClientException(String message) {
        super("XMLRepositoryClientException " + message);
    }

    public XMLRepositoryClientException(String message, Throwable cause) {
        super("XMLRepositoryClientException " + message, cause);
    }

    public XMLRepositoryClientException(Throwable cause) {
        super(cause);
    }
}