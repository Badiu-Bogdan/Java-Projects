package repository;

public class RepositoryException extends RuntimeException {
    public RepositoryException(String message) {
        super("RepositoryException " + message);
    }

    public RepositoryException(String message, Throwable cause) {
        super("RepositoryException " + message, cause);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }
}
