package repository.file;


import domain.BaseEntity;
import domain.Purchase.Purchase;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import repository.Repository;
import repository.file.exceptions.FileRepositoryStoreException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileRepositoryStore<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private String fileName; //ex: file_name.csv
    private Validator<T> validator;

    /**
     * Constructor of the class. Only set_up startup params.
     */
    public FileRepositoryStore(Validator<T> new_validator, String new_fileName) {
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
     * Save a new Purchase to the repository
     *
     * @param purchase : Purchase that will be saved to the repository
     * @throws FileRepositoryStoreException : -Description here!
     */
    public T saveToFile(Purchase purchase) throws FileRepositoryStoreException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true));
            String entity = getStringFromPurchase(purchase);

            File file = new File(this.fileName);
            if (file.length() == 0) //Here i have 2 cases: when i write into an empty file and when i append into one.
                writer.write(entity);
            else {
                writer.append(System.lineSeparator());
                writer.append(entity);
            }
            writer.close();
            return (T) purchase;
        } catch (Exception error) {
            throw new FileRepositoryStoreException("FileRepositoryPurchase->saveToFile:" + error.getMessage());
        }
    }

    /**
     * @param purchase
     * @return : A string with Purchase data separated by comma for writing into the file
     * @throws FileRepositoryStoreException: if Purchase in null or Purchase is invalid
     */
    public String getStringFromPurchase(Purchase purchase) throws FileRepositoryStoreException {
        if (purchase == null) {
            throw new FileRepositoryStoreException("FileRepositoryPurchase->getStringFromPurchase:" + " Purchase must be valid");
        }

        validator.validate((T) purchase);

        return purchase.getId() + "," + purchase.getSerialNumber() + "," + purchase.getClientId() +
                "," + purchase.getToyId() + "," + purchase.getPurchaseYear();
    }

    /**
     * Writes into the file a List of Purchases.(comma-separated)
     *
     * @param entities
     * @throws FileRepositoryStoreException: if there is an error in opening the file or while writing
     */
    public void saveEntitiesToFile(List<Purchase> entities) throws FileRepositoryStoreException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
            for (Purchase entity : entities) {
                writer.write(this.getStringFromPurchase(entity));
                writer.write(System.lineSeparator());
            }
            writer.close();

        } catch (IOException error) {
            throw new FileRepositoryStoreException("FileRepositoryPurchase->saveToFile:" + error.getMessage());
        }
    }


    /**
     * Read the file and get all the Purchases as a list from it
     *
     * @return : A list with all Purchases from the file
     * @throws FileRepositoryStoreException: If some error regarding reading the file occurs
     */
    public List<Purchase> readFile() throws FileRepositoryStoreException {
        /*
        arr[0] = Id
        arr[1] = serialNumber -String
        arr[2] = clientId -Long
        arr[3] = toyId -Long
        arr[4] = purchaseYear -int
         */
        Pattern pattern = Pattern.compile(",");

        try (Stream<String> lines = Files.lines(Path.of(this.fileName))) {
            List<Purchase> purchases = lines.map(line -> {
                String[] arr = pattern.split(line);
                Purchase purchase = new Purchase(arr[1],
                        Long.parseLong(arr[2]),
                        Long.parseLong(arr[3]),
                        Integer.parseInt(arr[4]));
                purchase.setId(Long.parseLong(arr[0]));
                return purchase;
            }).collect(Collectors.toList());
            return purchases;
        } catch (IOException error) {
            throw new FileRepositoryStoreException("readFile: NoSuchFileException");
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
    public Optional<T> findOne(ID id) throws FileRepositoryStoreException {
        if (id == null)
            throw new FileRepositoryStoreException("FileRepositoryException: id cannot be null");

        List<Purchase> Purchases = this.readFile();
        Map<Long, Purchase> map = Purchases.stream().collect(Collectors.toMap(Purchase::getId, Purchase -> Purchase));

        return (Optional<T>) Optional.ofNullable(map.get(id));
    }

    /**
     * @return all entities.
     */
    @Override
    public Iterable findAll() throws FileRepositoryStoreException {

        List<Purchase> Purchases = this.readFile();

        return (Iterable<T>) List.copyOf(Purchases);
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
    public Optional<T> save(T entity) throws ValidatorException, FileRepositoryStoreException {
        if (entity == null)
            throw new FileRepositoryStoreException("FileRepositoryException->save: id must not be null");

        validator.validate(entity);
        List<Purchase> entities = this.readFile();
        Map<Long, Purchase> map = entities.stream().collect(Collectors.toMap(Purchase::getId, Purchase -> Purchase));
        if (!map.containsKey(entity.getId())) {
            this.saveToFile((Purchase) entity);
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
    public Optional<T> delete(ID id) throws FileRepositoryStoreException {
        if (id == null)
            throw new FileRepositoryStoreException("FileRepositoryException->delete: id must not be null");
        if (this.findOne(id).isEmpty())
            return Optional.empty();

        List<Purchase> entities = this.readFile();
        Map<Long, Purchase> map = entities.stream().collect(Collectors.toMap(Purchase::getId, Purchase -> Purchase));
        Purchase result = map.remove(id);

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
    public Optional<T> update(T entity) throws ValidatorException, FileRepositoryStoreException {
        if (entity == null)
            throw new FileRepositoryStoreException("FileRepositoryException->update: id must not be null");
        this.validator.validate((T) entity);
        if (this.findOne(entity.getId()).isEmpty())
            return Optional.of(entity);

        this.delete(entity.getId());
        this.save(entity);
        return Optional.empty();
    }
}

