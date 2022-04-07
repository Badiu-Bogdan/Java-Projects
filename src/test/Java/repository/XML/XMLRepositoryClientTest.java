package repository.XML;

import domain.Client.Client;
import domain.validators.ClientValidator;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryClientException;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class XMLRepositoryClientTest {

    private static final Long ID = new Long(1);

    private Validator<Client> clientValidator;
    private Repository<Long, Client> clientRepository;

    @Before
    public void setUp() throws Exception {
        clientValidator = new ClientValidator();
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
    }

    @After
    public void tearDown() throws Exception {
        clientValidator=null;
        clientRepository=null;
    }

    /**
     * Tests finding a client from the repository.
     * A client is created, saved in the repository and then retrieved.
     *
     * @throws XMLRepositoryClientException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws XMLRepositoryClientException {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        assertFalse(clientRepository.save(client).isPresent());
        assertTrue(clientRepository.findOne(ID).isPresent());
        assertTrue(clientRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Client objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two clients)
     *
     * @throws XMLRepositoryClientException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws XMLRepositoryClientException {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
        Client client1 = new Client("50001","name1","addr1",2019);
        Client client2 = new Client("50002","name2","addr2",2020);
        client1.setId(ID);
        client2.setId(ID+1);
        assertFalse(clientRepository.save(client1).isPresent());
        assertFalse(clientRepository.save(client2).isPresent());
        Iterable<Client> clients = clientRepository.findAll();
        Iterator<Client> iterator = clients.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(clientRepository.delete(ID).isPresent());
        assertTrue(clientRepository.delete(ID+1).isPresent());
    }

    /**
     * Tests whether a client can be saved in the repository.
     * (should result in no exceptions)
     *
     */
    @Test
    public void testSave() {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
        Client client1 = new Client("50001","name1","addr1",2019);
        try{
            clientRepository.save(null);
            fail();
        }catch(IllegalArgumentException e){ }

        client1.setId(ID+100);

        try{
            client1.setYearOfRegistration(-200);
            clientRepository.save(client1);
            fail();
        }catch(ValidatorException e){
            client1.setYearOfRegistration(2019);
        }

        assertFalse(clientRepository.save(client1).isPresent());
        assertTrue(clientRepository.findOne(ID+100).isPresent());

        Client client2 = new Client("50002","name2","addr2",2020);
        client2.setId(ID+100);
        assertTrue(clientRepository.save(client2).isPresent());

        clientRepository.delete(client1.getId());
    }

    /**
     * Tests whether a client can be removed from the repository.
     * (should result in no exceptions)
     *
     */
    @Test
    public void testDelete() {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
        Client client1 = new Client("50001","name1","addr1",2019);
        client1.setId(ID);
        assertFalse(clientRepository.save(client1).isPresent());
        assertTrue(clientRepository.findOne(ID).isPresent());
        assertTrue(clientRepository.delete(ID).isPresent());
        assertFalse(clientRepository.findOne(ID).isPresent());
        assertFalse(clientRepository.delete(ID).isPresent());

        try{
            clientRepository.delete(null);
            fail();
        }catch(IllegalArgumentException e) {}
    }

    /**
     * Tests whether a client can be removed from the repository.
     * (should result in an exception)
     *
     * @throws IllegalArgumentException
     * if the delete did not succeed
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteException() throws IllegalArgumentException {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
        Client client1 = new Client("50001","name1","addr1",2019);
        clientRepository.delete(null);
    }

    /**
     * Tests whether a client can be updated in the repository.
     * A new client is created and updated in the repository.
     * Then the old client is retrieved from the repository (should not be found)
     * and the new client is retrieved from the repository as well (should be found)
     *
     */
    @Test
    public void testUpdate() {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
        Client client1 = new Client("50001","name1","addr1",2019);
        client1.setId(ID);
        assertFalse(clientRepository.save(client1).isPresent());
        assertTrue(clientRepository.findOne(ID).isPresent());
        Client client2 = new Client("50002","name2","addr2",2020);
        client2.setId(ID);
        assertFalse(clientRepository.update(client2).isPresent()); // should succeed
        assertTrue(clientRepository.findOne(ID).isPresent());
        clientRepository.delete(ID);
    }

    /**
     * Tests whether a client can be updated in the repository.
     * (should result in an exception)
     *
     * @throws ValidatorException
     * if the update did not succeed
     */
    @Test(expected = ValidatorException.class)
    public void testUpdateException() throws ValidatorException {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
        try{
            clientRepository.update(null);
            fail();
        }catch(IllegalArgumentException e) {}

        Client client1 = new Client("50001","name1","addr1",2019);
        client1.setId(ID);
        client1.setYearOfRegistration(-100);
        clientRepository.update(client1);
    }

    /**
     * Tests whether the file name was retrieved correctly
     */
    @Test
    public void getFileName() {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "testFileName");
        assertSame("testFileName", ((XMLRepositoryClient) clientRepository).getFileName());
    }

    /**
     * Tests whether the file name was set correctly
     */
    @Test
    public void setFileName() {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "testFileName");
        ((XMLRepositoryClient) clientRepository).setFileName("testFileName1");
        assertSame("testFileName1", ((XMLRepositoryClient) clientRepository).getFileName());
    }

    /**
     * Tests whether a client can be saved to xml format
     *
     */
    @Test
    public void saveToXML() {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
        Client client1 = new Client("50001","name1","addr1",2018);
        client1.setId(ID);
        ((XMLRepositoryClient)clientRepository).saveToXML(client1);
        assertTrue(clientRepository.delete(client1.getId()).isPresent());
    }

    /**
     * Tests whether the repository can remove all client instances from the xml file
     *
     */
    @Test
    public void deleteAll() {
        clientRepository = new XMLRepositoryClient<>(clientValidator, "test/clientsTest");
        ((XMLRepositoryClient)clientRepository).deleteAll();
    }

    /**
     * Tests whether more clients can be saved to xml format (from a list)
     *
     */
    @Test
    public void saveEntitiesToXML() {
        List<Client> clients = new ArrayList<>();
        Client client1 = new Client("50001","name1","addr1",2018);
        Client client2 = new Client("50002","name2","addr2",2019);
        Client client3 = new Client("50003","name3","addr3",2020);
        client1.setId(ID);
        client2.setId(ID+1);
        client3.setId(ID+2);
        clients.add(client1);
        clients.add(client2);
        clients.add(client3);
        ((XMLRepositoryClient)clientRepository).saveEntitiesToXML(clients);
        ((XMLRepositoryClient)clientRepository).deleteAll();
    }

    /**
     * Tests whether more clients can be saved to xml format (from a map)
     *
     */
    @Test
    public void saveMapToXML() {
        Map<Long, Client> clients = new HashMap<>();
        Client client1 = new Client("50001","name1","addr1",2018);
        Client client2 = new Client("50002","name2","addr2",2019);
        Client client3 = new Client("50003","name3","addr3",2020);
        client1.setId(ID);
        client2.setId(ID+1);
        client3.setId(ID+2);
        clients.put(client1.getId(), client1);
        clients.put(client2.getId(), client2);
        clients.put(client3.getId(), client3);
        ((XMLRepositoryClient)clientRepository).saveMapToXML(clients);
        ((XMLRepositoryClient)clientRepository).deleteAll();
    }
}