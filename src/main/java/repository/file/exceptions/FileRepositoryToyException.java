package repository.file.exceptions;

public class FileRepositoryToyException extends FileRepositoryException{
    public FileRepositoryToyException(String message)
    {
        super("FileRepositoryToyException" + message);
    }

    public FileRepositoryToyException(String message, Throwable cause)
    {
        super("FileRepositoryToyException" + message, cause);
    }

    public FileRepositoryToyException(Throwable cause)
    {
        super(cause);
    }
}
