package repository.XML.exceptions;

public class XMLRepositoryPetException extends XMLRepositoryException {

    public XMLRepositoryPetException(String message) {
        super("XMLRepositoryPetException " + message);
    }

    public XMLRepositoryPetException(String message, Throwable cause) {
        super("XMLRepositoryPetException " + message, cause);
    }

    public XMLRepositoryPetException(Throwable cause) {
        super(cause);
    }
}