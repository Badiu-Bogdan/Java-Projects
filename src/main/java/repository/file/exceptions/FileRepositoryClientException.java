package repository.file.exceptions;

public class FileRepositoryClientException extends FileRepositoryException{
    public FileRepositoryClientException(String message)
    {
        super("FileRepositoryClientException" + message);
    }

    public FileRepositoryClientException(String message, Throwable cause)
    {
        super("FileRepositoryClientException" + message, cause);
    }

    public FileRepositoryClientException(Throwable cause)
    {
        super(cause);
    }
}
