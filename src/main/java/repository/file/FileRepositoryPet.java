package repository.file;


import domain.BaseEntity;
import domain.Pet.Pet;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryPetException;
import repository.file.exceptions.FileRepositoryAdoptionException;
import repository.file.exceptions.FileRepositoryPetException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileRepositoryPet<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private String fileName; //ex: file_name.csv
    private Validator<T> validator;

    /**
     * Constructor of the class. Only set_up startup params.
     */
    public FileRepositoryPet(Validator<T> new_validator, String new_fileName) {
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
     * Save a new Pet to the repository
     *
     * @param pet : Pet that will be saved to the repository
     * @throws FileRepositoryPetException : -Description here!
     */
    public T saveToFile(Pet pet) throws FileRepositoryPetException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true));
            String entity = getStringFromPet(pet);

            File file = new File(this.fileName);
            if (file.length() == 0) //Here i have 2 cases: when i write into an empty file and when i append into one.
                writer.write(entity);
            else {
                writer.append(System.lineSeparator());
                writer.append(entity);
            }
            writer.close();
            return (T) pet;
        } catch (Exception error) {
            throw new FileRepositoryPetException("FileRepositoryPet->saveToFile:" + error.getMessage());
        }
    }

    /**
     * @param pet
     * @return : A string with Pet data separated by comma for writing into the file
     * @throws FileRepositoryPetException: if Pet in null or Pet is invalid
     */
    public String getStringFromPet(Pet pet) throws FileRepositoryPetException {
        if (pet == null) {
            throw new FileRepositoryPetException("FileRepositoryPet->getStringFromPet:" + " Pet must be valid");
        }

        validator.validate((T) pet);

        return pet.getId() + "," + pet.getSerialNumber() + "," + pet.getName() +
                "," + pet.getBreed() + "," + pet.getBirthDate();
    }

    /**
     * Writes into the file a List of Pets.(comma-separated)
     *
     * @param entities
     * @throws FileRepositoryPetException: if there is an error in opening the file or while writing
     */
    public void saveEntitiesToFile(List<Pet> entities) throws FileRepositoryPetException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
            for (Pet entity : entities) {
                writer.write(this.getStringFromPet(entity));
                writer.write(System.lineSeparator());
            }
            writer.close();

        } catch (IOException error) {
            throw new FileRepositoryPetException("FileRepositoryPet->saveToFile:" + error.getMessage());
        }
    }


    /**
     * Read the file and get all the Pets as a list from it
     *
     * @return : A list with all Pets from the file
     * @throws FileRepositoryPetException: If some error regarding reading the file occurs
     */
    public List<Pet> readFile() throws FileRepositoryPetException {
        /*
        arr[0] = Id
        arr[1] = serialNumber -String
        arr[2] = name -String
        arr[3] = breed -String
        arr[4] = birthDate -int
         */
        Pattern pattern = Pattern.compile(",");

        try (Stream<String> lines = Files.lines(Path.of(this.fileName))) {
            List<Pet> pets = lines.map(line -> {
                String[] arr = pattern.split(line);
                Pet pet = new Pet(arr[1],
                        arr[2],
                        arr[3],
                        Integer.parseInt(arr[4]));
                pet.setId(Long.parseLong(arr[0]));
                return pet;
            }).collect(Collectors.toList());
            return pets;
        } catch (IOException error) {
            throw new FileRepositoryPetException("readFile: NoSuchFileException");
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
    public Optional<T> findOne(ID id) throws FileRepositoryPetException {
        if (id == null)
            throw new FileRepositoryPetException("FileRepositoryException->findOne: id cannot be null");

        List<Pet> pets = this.readFile();
        Map<Long, Pet> map = pets.stream().collect(Collectors.toMap(Pet::getId, Pet -> Pet));

        return (Optional<T>) Optional.ofNullable(map.get(id));
    }


    /**
     * @return all entities.
     */
    @Override
    public Iterable findAll() throws FileRepositoryPetException {

        List<Pet> pets = this.readFile();

        return (Iterable<T>) List.copyOf(pets);
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
    public Optional<T> save(T entity) throws ValidatorException, FileRepositoryPetException {
        if (entity == null)
            throw new FileRepositoryPetException("FileRepositoryException->save: id must not be null");

        validator.validate(entity);
        List<Pet> entities = this.readFile();
        Map<Long, Pet> map = entities.stream().collect(Collectors.toMap(Pet::getId, Pet -> Pet));
        if (!map.containsKey(entity.getId())) {
            this.saveToFile((Pet) entity);
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
    public Optional<T> delete(ID id) throws FileRepositoryPetException {
        if (id == null)
            throw new FileRepositoryPetException("FileRepositoryException->delete: id must not be null");
        if (this.findOne(id).isEmpty())
            return Optional.empty();

        List<Pet> entities = this.readFile();
        Map<Long, Pet> map = entities.stream().collect(Collectors.toMap(Pet::getId, Pet -> Pet));
        Pet result = map.remove(id);

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
    public Optional<T> update(T entity) throws ValidatorException, FileRepositoryPetException {
        if (entity == null)
            throw new FileRepositoryAdoptionException("FileRepositoryException->update: id must not be null");
        validator.validate(entity);
        if (this.findOne(entity.getId()).isEmpty())
            return Optional.of(entity);

        List<Pet> entities = this.readFile();
        Map<Long, Pet> map = entities.stream().collect(Collectors.toMap(Pet::getId, Pet -> Pet));
        this.delete(entity.getId());
        this.save(entity);
        return Optional.empty();
    }
}
