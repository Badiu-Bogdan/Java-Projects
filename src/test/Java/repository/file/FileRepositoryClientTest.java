package repository.file;
import domain.Client.Client;
import domain.validators.ClientValidator;
import domain.validators.Validator;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryClientException;
import repository.file.exceptions.FileRepositoryClientException;

import java.io.*;
import java.util.Iterator;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FileRepositoryClientTest {
    private static final Long ID = new Long(1);

    private Validator<Client> ClientValidator;
    private Repository<Long, Client> ClientRepository;


    @BeforeEach
    public void setup() throws Exception {
        ClientValidator = new ClientValidator();
        ClientRepository = new FileRepositoryClient(ClientValidator, "data/file/test/clientsTest.csv");

        Client first = new Client("3333", "Vasile", "Sibiu", 2000);
        first.setId(1L);
        Client second = new Client("4444", "Andrei", "Bucharest", 2021);
        second.setId(2L);
        Client third = new Client("5555", "Gheorghe", "Pitesti", 2014);
        third.setId(3L);
        Client forth = new Client("6666", "Tomas", "Mures", 2010);
        forth.setId(4L);
    }

    @AfterEach
    public void teardown() throws Exception {
        ClientValidator = null;
        ClientRepository = null;
        new FileWriter("data/file/test/clientsTest.csv");
    }


    @Test
    public void testGetFileName() {
        assertSame("data/file/test/clientsTest.csv", ((FileRepositoryClient) ClientRepository).getFileName());
    }

    @Test
    public void setFileName() {
        ((FileRepositoryClient) ClientRepository).setFileName("Ana are mere");
        assertSame("Ana are mere", ((FileRepositoryClient) ClientRepository).getFileName());
    }

    /**
     * Test the reading process into the file.
     * A Client is created, saved into the file and then the file is read.
     *
     * @throws FileRepositoryClientException if the file can't be opened
     */
    @Test
    public void testSaveToFile() throws FileRepositoryClientException {
/*        Client first = new Client("3333", 1L, 2L, 2000);
        first.setId(1L);
        ((FileRepositoryClient)ClientRepository).saveToFile(first);

        try(Stream<String> stream = Files.lines(Paths.get("data/file/test/ClientsTest.csv"))) {

        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /**
     * Test whether the Client is converted without faults into a comma separated string.
     *
     * @throws FileRepositoryClientException
     */
    @Test
    public void testGetStringFromClient() throws FileRepositoryClientException {
        Client first = new Client("3333", "Vasile", "Sibiu", 2000);
        first.setId(1L);
        String supposed_result = "1,3333,Vasile,Sibiu,2000";
        assertTrue(((FileRepositoryClient) ClientRepository).getStringFromClient(first).equals(supposed_result));

    }

/*    @Test
    public void testSaveEntitiesToFile() throws FileRepositoryClientException {
        Client first = new Client("3333", 1L, 2L, 2000);
        first.setId(1L);
        Client second = new Client("4444", 2L, 3L, 2021);
        second.setId(2L);

        List<Client> Clients = new ArrayList<Client>();
        Clients.add(first);
        Clients.add(second);

        //((FileRepositoryClient) ClientRepository).save(first);
        //((FileRepositoryClient) ClientRepository).save(second);
        ((FileRepositoryClient) ClientRepository).saveEntitiesToFile(Clients);
        Path filename = Path.of("data/file/test/ClientsTest.csv");

        try {
            String content = Files.readString(filename);
            assertTrue(content.equals("1,3333,1,2,2000\n2,4444,2,3,2021"));

        } catch (IOException error) {
            throw new FileRepositoryClientException(error);
        }

    }*/

    @Test
    public void testReadFile() throws FileRepositoryClientException {

    }

    /**
     * Tests finding a Client from the repository.
     * A Client is created, saved in the repository and then retrieved.
     *
     * @throws XMLRepositoryClientException if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws FileRepositoryClientException {
        Client first = new Client("3333", "Vasile", "Sibiu", 2000);
        first.setId(1L);
        ClientRepository.save(first);
        assertTrue(ClientRepository.findOne(ID).isPresent());
        assertTrue(ClientRepository.delete(ID).isPresent());
        assertFalse(ClientRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether the method find all from the repository is getting out the right list of entities.
     * I'm adding 2 entities into repo.
     * Then call findall which should return an Iterable object that contains 2 elements.
     *
     * @throws FileRepositoryClientException
     */
    @Test
    public void testFindAll() throws FileRepositoryClientException {
        Client first = new Client("3333", "Vasile", "Sibiu", 2000);
        first.setId(1L);
        Client second = new Client("4444", "Andrei", "Bucharest", 2021);
        second.setId(2L);
        this.ClientRepository.save(first);
        this.ClientRepository.save(second);

        Iterable<Client> iterable = this.ClientRepository.findAll();
        Iterator<Client> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(this.ClientRepository.delete(first.getId()).isPresent());
        assertTrue(this.ClientRepository.delete(second.getId()).isPresent());
    }

    /**
     * Tests whether a Client can be saved in the repository.
     * (should result in no exceptions)
     *
     * @throws FileRepositoryClientException if the save operation did not succeed
     */
    @Test
    public void testSave() throws FileRepositoryClientException {
        Client first = new Client("3333", "Vasile", "Sibiu", 2000);
        first.setId(1L);
        Client second = new Client("4444", "Andrei", "Bucharest", 2021);
        second.setId(2L);
        Client third = new Client("5555", "Gheorghe", "Pitesti", 2014);
        third.setId(3L);
        Client forth = new Client("6666", "Tomas", "Mures", 2010);
        forth.setId(4L);

        Assert.assertFalse(ClientRepository.save(first).isPresent());
        Assert.assertFalse(ClientRepository.save(second).isPresent());
        Assert.assertFalse(ClientRepository.save(third).isPresent());
        Assert.assertFalse(ClientRepository.save(forth).isPresent());
        Assert.assertTrue(ClientRepository.save(forth).isPresent());

        assertTrue(ClientRepository.findOne(first.getId()).isPresent());
        assertTrue(ClientRepository.findOne(second.getId()).isPresent());
        assertTrue(ClientRepository.findOne(third.getId()).isPresent());
        assertTrue(ClientRepository.findOne(forth.getId()).isPresent());

    }

    /**
     * Tests wheter an Client can be deleted from the repository.
     * A Client is created, added into repo, searched to see if
     * it's actually there and then deleted.
     * Before delete another find is performed and the Client should
     * not exist anymore.
     *
     * @throws FileRepositoryClientException
     */
    @Test
    public void testDelete() throws FileRepositoryClientException {
        Client first = new Client("3333", "Vasile", "Sibiu", 2000);
        first.setId(1L);
        ClientRepository.save(first);
        assertTrue(ClientRepository.findOne(1L).isPresent());
        assertTrue(ClientRepository.delete(1L).isPresent());
        assertFalse(ClientRepository.findOne(1L).isPresent());
    }

    /**
     * @throws FileRepositoryClientException
     */
    @Test
    public void testUpdate() throws FileRepositoryClientException {
        Client first = new Client("3333", "Vasile", "Sibiu", 2000);
        first.setId(1L);
        assertFalse(ClientRepository.save(first).isPresent());
        assertTrue(ClientRepository.findOne(1L).isPresent());

        Client second = new Client("4444", "Andrei", "Bucharest", 2021);;
        second.setId(1L);
        assertFalse(ClientRepository.update(second).isPresent());
        assertTrue(ClientRepository.findOne(1L).isPresent());

        Client forth = new Client("6666", "Tomas", "Mures", 2010);
        forth.setId(4L);
        assertTrue(ClientRepository.update(forth).isPresent());
    }
}
