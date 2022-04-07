package repository.DB.exceptions;

public class DBRepositoryAdoptionException extends DBRepositoryException {

    public DBRepositoryAdoptionException(String message) {
        super("DBRepositoryAdoptionException " + message);
    }

    public DBRepositoryAdoptionException(String message, Throwable cause) {
        super("DBRepositoryAdoptionException " + message, cause);
    }

    public DBRepositoryAdoptionException(Throwable cause) {
        super(cause);
    }
}