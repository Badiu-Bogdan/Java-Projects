package repository.file;

import domain.Adoption.Adoption;
import domain.BaseEntity;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryAdoptionException;
import repository.file.exceptions.FileRepositoryAdoptionException;
import repository.file.exceptions.FileRepositoryClientException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileRepositoryAdoption<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private String fileName; //ex: file_name.csv
    private Validator<T> validator;

    /**
     * Constructor of the class. Only set_up startup params.
     */
    public FileRepositoryAdoption(Validator<T> new_validator, String new_fileName) {
        this.validator = new_validator;
        this.fileName = new_fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String new_fileName) {
        this.fileName = new_fileName;
    }


    /**
     * Save a new adoption to the repository
     *
     * @param adoption : Adoption that will be saved to the repository
     * @throws FileRepositoryAdoptionException : -Description here!
     */
    public T saveToFile(Adoption adoption) throws FileRepositoryAdoptionException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true));
            String entity = getStringFromAdoption(adoption);

            File file = new File(this.fileName);
            if (file.length() == 0) //Here i have 2 cases: when i write into an empty file and when i append into one.
                writer.write(entity);
            else {
                writer.append(System.lineSeparator());
                writer.append(entity);
            }
            writer.close();
            return (T) adoption;
        } catch (Exception error) {
            throw new FileRepositoryAdoptionException("FileRepositoryAdoption->saveToFile:" + error.getMessage());
        }
    }

    /**
     * @param adoption
     * @return : A string with adoption data separated by comma for writing into the file
     * @throws FileRepositoryAdoptionException: if adoption in null or adoption is invalid
     */
    public String getStringFromAdoption(Adoption adoption) throws FileRepositoryAdoptionException {
        if (adoption == null) {
            throw new FileRepositoryAdoptionException("FileRepositoryAdoption->getStringFromAdoption:" + " adoption must be valid");
        }

        validator.validate((T) adoption);

        return adoption.getId() + "," + adoption.getSerialNumber() + "," + String.valueOf(adoption.getClientId()) +
                "," + String.valueOf(adoption.getPetId()) + "," + String.valueOf(adoption.getAdoptionYear());
    }

    /**
     * Writes into the file a List of Adoptions.(comma-separated)
     *
     * @param entities
     * @throws FileRepositoryAdoptionException: if there is an error in opening the file or while writing
     */
    public void saveEntitiesToFile(List<Adoption> entities) throws FileRepositoryAdoptionException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
            for (Adoption entity : entities) {
                writer.write(this.getStringFromAdoption(entity));
                writer.write(System.lineSeparator());
            }
            writer.close();

        } catch (IOException error) {
            throw new FileRepositoryAdoptionException("FileRepositoryAdoption->saveToFile:" + error.getMessage());
        }
    }


    /**
     * Read the file and get all the Adoptions as a list from it
     *
     * @return : A list with all adoptions from the file
     * @throws FileRepositoryAdoptionException: If some error regarding reading the file occurs
     */
    public List<Adoption> readFile() throws FileRepositoryAdoptionException {
        /*
        arr[0] = Id
        arr[1] = serialNumber
        arr[2] = clientId
        arr[3] = petId
        arr[4] = adoptionYear
         */
        Pattern pattern = Pattern.compile(",");

        try (Stream<String> lines = Files.lines(Path.of(this.fileName))) {
            List<Adoption> adoptions = lines.map(line -> {
                String[] arr = pattern.split(line);
                Adoption adoption = new Adoption(arr[1],
                        Long.parseLong(arr[2]),
                        Long.parseLong(arr[3]),
                        Integer.parseInt(arr[4]));
                adoption.setId(Long.parseLong(arr[0]));
                return adoption;
            }).collect(Collectors.toList());
            return adoptions;
        } catch (IOException error) {
            throw new FileRepositoryAdoptionException("readFile: NoSuchFileException");
        }
    }

    /**
     * Find the entity with the given {@code id}.
     *
     * @param id must be not null.
     * @return an {@code Optional} encapsulating the entity with the given id.
     * @throws IllegalArgumentException if the given id is null.
     */
    @Override
    public Optional<T> findOne(ID id) throws FileRepositoryAdoptionException {
        if (id == null)
            throw new FileRepositoryAdoptionException("FileRepositoryException: id cannot be null");

        List<Adoption> adoptions = this.readFile();
        Map<Long, Adoption> map = adoptions.stream().collect(Collectors.toMap(Adoption::getId, Adoption -> Adoption));

        return (Optional<T>) Optional.ofNullable(map.get(id));
    }

    /**
     * @return all entities.
     */
    @Override
    public Iterable findAll() throws FileRepositoryAdoptionException {

        List<Adoption> adoptions = this.readFile();

        return (Iterable<T>) List.copyOf(adoptions);
    }

    /**
     * Saves the given entity.
     *
     * @param entity must not be null.
     * @return an {@code Optional} - null if the entity was saved otherwise (e.g. id already exists) returns the entity.
     * @throws IllegalArgumentException if the given entity is null.
     * @throws ValidatorException       if the entity is not valid.
     */
    @Override
    public Optional<T> save(T entity) throws ValidatorException, FileRepositoryAdoptionException {
        if (entity == null)
            throw new FileRepositoryAdoptionException("FileRepositoryException->save: id must not be null");

        validator.validate(entity);
        List<Adoption> entities = this.readFile();
        Map<Long, Adoption> map = entities.stream().collect(Collectors.toMap(Adoption::getId, Adoption -> Adoption));
        if (!map.containsKey(entity.getId())) {
            this.saveToFile((Adoption) entity);
            return Optional.empty();
        } else {
            return (Optional<T>) Optional.ofNullable(map.get(entity.getId()));
        }
    }

    /**
     * Removes the entity with the given id.
     *
     * @param id must not be null.
     * @return an {@code Optional} - null if there is no entity with the given id, otherwise the removed entity.
     * @throws IllegalArgumentException if the given id is null.
     */
    @Override
    public Optional<T> delete(ID id) throws FileRepositoryAdoptionException {
        if (id == null)
            throw new FileRepositoryAdoptionException("FileRepositoryException->delete: id must not be null");
        if (this.findOne(id).isEmpty())
            return Optional.empty();

        List<Adoption> entities = this.readFile();
        Map<Long, Adoption> map = entities.stream().collect(Collectors.toMap(Adoption::getId, Adoption -> Adoption));
        Adoption result = map.remove(id);

        saveEntitiesToFile(new ArrayList<>(map.values()));
        return (Optional<T>) Optional.of(result);
    }

    /**
     * Updates the given entity.
     *
     * @param entity must not be null.
     * @return an {@code Optional} - null if the entity was updated otherwise (e.g. id does not exist) returns the
     * entity.
     * @throws IllegalArgumentException if the given entity is null.
     * @throws ValidatorException       if the entity is not valid.
     */
    @Override
    public Optional<T> update(T entity) throws FileRepositoryAdoptionException {
        if (entity == null)
            throw new FileRepositoryAdoptionException("FileRepositoryException->update: id must not be null");
        validator.validate(entity);
        if (this.findOne(entity.getId()).isEmpty())
            return Optional.of(entity);

        this.delete(entity.getId());
        this.save(entity);
        return Optional.empty();
    }
}
