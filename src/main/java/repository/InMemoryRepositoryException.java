package repository;

public class InMemoryRepositoryException extends RepositoryException {
    public InMemoryRepositoryException(String message) {
        super("InMemoryRepositoryException " + message);
    }

    public InMemoryRepositoryException(String message, Throwable cause) {
        super("InMemoryRepositoryException " + message, cause);
    }

    public InMemoryRepositoryException(Throwable cause) {
        super(cause);
    }
}
