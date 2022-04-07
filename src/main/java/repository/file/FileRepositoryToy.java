package repository.file;

import domain.BaseEntity;
import domain.Toy.Toy;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryToyException;
import repository.file.exceptions.FileRepositoryToyException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileRepositoryToy<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private String fileName; //ex: file_name.csv
    private Validator<T> validator;

    /**
     * Constructor of the class. Only set_up startup params.
     */
    public FileRepositoryToy(Validator<T> new_validator, String new_fileName) {
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
     * Save a new Toy to the repository
     *
     * @param toy : Toy that will be saved to the repository
     * @throws FileRepositoryToyException : -Description here!
     */
    public T saveToFile(Toy toy) throws FileRepositoryToyException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true));
            String entity = getStringFromToy(toy);

            File file = new File(this.fileName);
            if (file.length() == 0) //Here i have 2 cases: when i write into an empty file and when i append into one.
                writer.write(entity);
            else {
                writer.append(System.lineSeparator());
                writer.append(entity);
            }
            writer.close();
            return (T) toy;
        } catch (Exception error) {
            throw new FileRepositoryToyException("FileRepositoryToy->saveToFile:" + error.getMessage());
        }
    }

    /**
     * @param toy
     * @return : A string with Toy data separated by comma for writing into the file
     * @throws FileRepositoryToyException: if Toy in null or Toy is invalid
     */
    public String getStringFromToy(Toy toy) throws FileRepositoryToyException {
        if (toy == null) {
            throw new FileRepositoryToyException("FileRepositoryToy->getStringFromToy:" + " Toy must be valid");
        }

        validator.validate((T) toy);

        return toy.getId() + "," + toy.getSerialNumber() + "," + toy.getName() +
                "," + toy.getWeight() + "," + toy.getMaterial() + "," + toy.getPrice();
    }

    /**
     * Writes into the file a List of Toys.(comma-separated)
     *
     * @param entities
     * @throws FileRepositoryToyException: if there is an error in opening the file or while writing
     */
    public void saveEntitiesToFile(List<Toy> entities) throws FileRepositoryToyException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
            for (Toy entity : entities) {
                writer.write(this.getStringFromToy(entity));
                writer.write(System.lineSeparator());
            }
            writer.close();

        } catch (IOException error) {
            throw new FileRepositoryToyException("FileRepositoryToy->saveToFile:" + error.getMessage());
        }
    }


    /**
     * Read the file and get all the Toys as a list from it
     *
     * @return : A list with all Toys from the file
     * @throws FileRepositoryToyException: If some error regarding reading the file occurs
     */
    public List<Toy> readFile() throws FileRepositoryToyException {
        /*
        arr[0] = Id
        arr[1] = serialNumber -String
        arr[2] = name -String
        arr[3] = weight -int
        arr[4] = material -String
        arr[5] = price -double
         */
        Pattern pattern = Pattern.compile(",");

        try (Stream<String> lines = Files.lines(Path.of(this.fileName))) {
            List<Toy> toys = lines.map(line -> {
                String[] arr = pattern.split(line);
                Toy toy = new Toy(arr[1],
                        arr[2],
                        Integer.parseInt(arr[3]),
                        arr[4],
                        Double.parseDouble(arr[5]));
                toy.setId(Long.parseLong(arr[0]));
                return toy;
            }).collect(Collectors.toList());
            return toys;
        } catch (IOException error) {
            throw new FileRepositoryToyException("readFile: NoSuchFileException");
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
    public Optional<T> findOne(ID id) throws FileRepositoryToyException {
        if (id == null)
            throw new FileRepositoryToyException("FileRepositoryException: id cannot be null");

        List<Toy> toys = this.readFile();
        Map<Long, Toy> map = toys.stream().collect(Collectors.toMap(Toy::getId, Toy -> Toy));

        return (Optional<T>) Optional.ofNullable(map.get(id));
    }


    /**
     * @return all entities.
     */
    @Override
    public Iterable findAll() throws FileRepositoryToyException {

        List<Toy> toys = this.readFile();

        return (Iterable<T>) List.copyOf(toys);
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
    public Optional<T> save(T entity) throws ValidatorException, FileRepositoryToyException {
        if (entity == null)
            throw new FileRepositoryToyException("FileRepositoryException->save: id must not be null");

        validator.validate(entity);
        List<Toy> entities = this.readFile();
        Map<Long, Toy> map = entities.stream().collect(Collectors.toMap(Toy::getId, Toy -> Toy));
        if (!map.containsKey(entity.getId())) {
            this.saveToFile((Toy) entity);
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
    public Optional<T> delete(ID id) throws FileRepositoryToyException {
        if (id == null)
            throw new FileRepositoryToyException("FileRepositoryException->save: id must not be null");
        if (this.findOne(id).isEmpty())
            return Optional.empty();

        List<Toy> entities = this.readFile();
        Map<Long, Toy> map = entities.stream().collect(Collectors.toMap(Toy::getId, Toy -> Toy));
        Toy result = map.remove(id);

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
    public Optional<T> update(T entity) throws ValidatorException, FileRepositoryToyException {
        if (entity == null)
            throw new FileRepositoryToyException("FileRepositoryException->update: id must not be null");
        this.validator.validate((T) entity);
        if (this.findOne(entity.getId()).isEmpty())
            return Optional.of(entity);

        this.delete(entity.getId());
        this.save(entity);
        return Optional.empty();
    }
}

