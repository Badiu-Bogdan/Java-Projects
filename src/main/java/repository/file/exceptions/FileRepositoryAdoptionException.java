package repository.file.exceptions;

public class FileRepositoryAdoptionException extends FileRepositoryException{

    public FileRepositoryAdoptionException(String message)
    {
        super("FIleRepositoryAdoptionException" + message);
    }

    public FileRepositoryAdoptionException(String message, Throwable cause)
    {
        super("FIleRepositoryAdoptionException" + message, cause);
    }

    public FileRepositoryAdoptionException(Throwable cause)
    {
        super(cause);
    }
}
