package repository.file;

import domain.BaseEntity;
import domain.Client.Client;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryClientException;
import repository.file.exceptions.FileRepositoryClientException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileRepositoryClient<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private String fileName; //ex: file_name.csv
    private Validator<T> validator;

    /**
     * Constructor of the class. Only set_up startup params.
     */
    public FileRepositoryClient(Validator<T> new_validator, String new_fileName) {
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
     * Save a new Client to the repository
     *
     * @param client : Client that will be saved to the repository
     * @throws FileRepositoryClientException : -Description here!
     */
    public T saveToFile(Client client) throws FileRepositoryClientException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true));
            String entity = getStringFromClient(client);

            File file = new File(this.fileName);
            if (file.length() == 0) //Here i have 2 cases: when i write into an empty file and when i append into one.
                writer.write(entity);
            else {
                writer.append(System.lineSeparator());
                writer.append(entity);
            }
            writer.close();
            return (T) client;
        } catch (Exception error) {
            throw new FileRepositoryClientException("FileRepositoryClient->saveToFile:" + error.getMessage());
        }
    }

    /**
     * @param client
     * @return : A string with Client data separated by comma for writing into the file
     * @throws FileRepositoryClientException: if Client in null or Client is invalid
     */
    public String getStringFromClient(Client client) throws FileRepositoryClientException {
        if (client == null) {
            throw new FileRepositoryClientException("FileRepositoryClient->getStringFromClient:" + " client must be valid");
        }

        validator.validate((T) client);

        return client.getId() + "," + client.getSerialNumber() + "," + client.getName() +
                "," + client.getAddress() + "," + String.valueOf(client.getYearOfRegistration());
    }

    /**
     * Writes into the file a List of Clients.(comma-separated)
     *
     * @param entities
     * @throws FileRepositoryClientException: if there is an error in opening the file or while writing
     */
    public void saveEntitiesToFile(List<Client> entities) throws FileRepositoryClientException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
            for (Client entity : entities) {
                writer.write(this.getStringFromClient(entity));
                writer.write(System.lineSeparator());
            }
            writer.close();

        } catch (IOException error) {
            throw new FileRepositoryClientException("FileRepositoryClient->saveToFile:" + error.getMessage());
        }
    }


    /**
     * Read the file and get all the Clients as a list from it
     *
     * @return : A list with all clients from the file
     * @throws FileRepositoryClientException: If some error regarding reading the file occurs
     */
    public List<Client> readFile() throws FileRepositoryClientException {
        /*
        arr[0] = Id
        arr[1] = serialNumber
        arr[2] = name
        arr[3] = address
        arr[4] = yearOfRegistration
         */
        Pattern pattern = Pattern.compile(",");

        try (Stream<String> lines = Files.lines(Path.of(this.fileName))) {
            List<Client> clients = lines.map(line -> {
                String[] arr = pattern.split(line);
                Client client = new Client(arr[1],
                        arr[2],
                        arr[3],
                        Integer.parseInt(arr[4]));
                client.setId(Long.parseLong(arr[0]));
                return client;
            }).collect(Collectors.toList());
            return clients;
        } catch (IOException error) {
            throw new FileRepositoryClientException("readFile: NoSuchFileException");
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
    public Optional<T> findOne(ID id) throws FileRepositoryClientException {
        if (id == null)
            throw new FileRepositoryClientException("FileRepositoryException: id cannot be null");

        List<Client> clients = this.readFile();
        Map<Long, Client> map = clients.stream().collect(Collectors.toMap(Client::getId, Client -> Client));

        return (Optional<T>) Optional.ofNullable(map.get(id));
    }


    /**
     * @return all entities.
     */
    @Override
    public Iterable findAll() throws FileRepositoryClientException {

        List<Client> clients = this.readFile();

        return (Iterable<T>) List.copyOf(clients);
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
    public Optional<T> save(T entity) throws ValidatorException, FileRepositoryClientException {
        if (entity == null)
            throw new FileRepositoryClientException("FileRepositoryException->save: id must not be null");

        validator.validate(entity);
        List<Client> entities = this.readFile();
        Map<Long, Client> map = entities.stream().collect(Collectors.toMap(Client::getId, Client -> Client));
        if (!map.containsKey(entity.getId())) {
            this.saveToFile((Client) entity);
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
    public Optional<T> delete(ID id) throws FileRepositoryClientException {
        if (id == null)
            throw new FileRepositoryClientException("FileRepositoryException->delete: id must not be null");
        if (this.findOne(id).isEmpty())
            return Optional.empty();

        List<Client> entities = this.readFile();
        Map<Long, Client> map = entities.stream().collect(Collectors.toMap(Client::getId, Client -> Client));
        Client result = map.remove(id);

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
    public Optional<T> update(T entity) throws ValidatorException, FileRepositoryClientException {
        if (entity == null)
            throw new FileRepositoryClientException("FileRepositoryException->update: id must not be null");
        validator.validate(entity);
        if (this.findOne(entity.getId()).isEmpty())
            return Optional.of(entity);

        this.delete(entity.getId());
        this.save(entity);
        return Optional.empty();
    }
}

