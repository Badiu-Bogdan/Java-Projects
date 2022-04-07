package repository.file.exceptions;

public class FileRepositoryPetException extends FileRepositoryException{
    public FileRepositoryPetException(String message)
    {
        super("FileRepositoryPetException" + message);
    }

    public FileRepositoryPetException(String message, Throwable cause)
    {
        super("FileRepositoryPetException" + message, cause);
    }

    public FileRepositoryPetException(Throwable cause)
    {
        super(cause);
    }
}
