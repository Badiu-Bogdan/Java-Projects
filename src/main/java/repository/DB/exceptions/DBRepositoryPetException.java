package repository.DB.exceptions;

public class DBRepositoryPetException extends DBRepositoryException {

    public DBRepositoryPetException(String message) {
        super("DBRepositoryPetException " + message);
    }

    public DBRepositoryPetException(String message, Throwable cause) {
        super("DBRepositoryPetException " + message, cause);
    }

    public DBRepositoryPetException(Throwable cause) {
        super(cause);
    }
}