package repository.XML.exceptions;

public class XMLRepositoryAdoptionException extends XMLRepositoryException {

    public XMLRepositoryAdoptionException(String message) {
        super("XMLRepositoryAdoptionException " + message);
    }

    public XMLRepositoryAdoptionException(String message, Throwable cause) {
        super("XMLRepositoryAdoptionException " + message, cause);
    }

    public XMLRepositoryAdoptionException(Throwable cause) {
        super(cause);
    }
}