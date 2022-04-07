package repository.file.exceptions;

public class FileRepositoryStoreException extends FileRepositoryException{
    public FileRepositoryStoreException(String message)
    {
        super("FileRepositoryStoreException" + message);
    }

    public FileRepositoryStoreException(String message, Throwable cause)
    {
        super("FileRepositoryStoreException" + message, cause);
    }

    public FileRepositoryStoreException(Throwable cause)
    {
        super(cause);
    }

}
