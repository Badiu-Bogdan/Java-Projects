package repository.XML.exceptions;

public class XMLRepositoryStoreException extends XMLRepositoryException {

    public XMLRepositoryStoreException(String message) {
        super("XMLRepositoryStoreException " + message);
    }

    public XMLRepositoryStoreException(String message, Throwable cause) {
        super("XMLRepositoryStoreException " + message, cause);
    }

    public XMLRepositoryStoreException(Throwable cause) {
        super(cause);
    }
}