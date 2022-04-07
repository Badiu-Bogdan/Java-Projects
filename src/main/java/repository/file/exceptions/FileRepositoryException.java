package repository.file.exceptions;

import repository.RepositoryException;

public class FileRepositoryException extends RepositoryException {

    public FileRepositoryException(String message){ super(message); }

    public FileRepositoryException(String message, Throwable cause){ super(message,cause); }

    public FileRepositoryException(Throwable cause){ super(cause); }

}
