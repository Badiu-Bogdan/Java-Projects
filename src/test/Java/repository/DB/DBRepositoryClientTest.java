package repository.DB;

import domain.Client.Client;
import domain.validators.ClientValidator;
import domain.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.DB.exceptions.DBRepositoryClientException;
import repository.Repository;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class DBRepositoryClientTest {

    private static final Long ID = new Long(1);

    private Validator<Client> clientValidator;
    private Repository<Long, Client> clientRepository;

    @Before
    public void setUp() throws Exception {
        clientValidator = new ClientValidator();
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");
        ((DBRepositoryClient) clientRepository).dropTable();

    }

    @After
    public void tearDown() throws Exception {

        ((DBRepositoryClient) clientRepository).dropTable();
        clientValidator=null;
        clientRepository=null;

    }

    /**
     * Tests finding a client from the repository.
     * An client is created, saved in the repository and then retrieved.
     *
     * @throws DBRepositoryClientException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws DBRepositoryClientException {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");
        Client client = new Client("50001","name1","address1",2019);
        client.setId(ID);
        assertTrue(clientRepository.save(client).isPresent());
        assertTrue(clientRepository.findOne(ID).isPresent());
        assertTrue(clientRepository.delete(ID).isPresent());

    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Client objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two clients)
     *
     * @throws DBRepositoryClientException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws DBRepositoryClientException {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");
        Client client1 = new Client("50001","name1","address1",2019);
        Client client2 = new Client("50002","name2","address2",2020);
        client1.setId(ID);
        client2.setId(ID+1);
        assertTrue(clientRepository.save(client1).isPresent());
        assertTrue(clientRepository.save(client2).isPresent());
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
     * @throws DBRepositoryClientException
     * if the save operation did not succeed
     */
    @Test
    public void testSave() throws DBRepositoryClientException {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testtablename");
        Client client1 = new Client("50001","name1","address1",2019);
        client1.setId(ID);
        assertTrue(clientRepository.save(client1).isPresent());
        assertTrue(clientRepository.findOne(ID).isPresent());
        assertTrue(clientRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the same client can be saved in the repository twice.
     * (should catch an exception)
     *
     * @throws DBRepositoryClientException
     * if the save operation did not succeed
     */
    @Test(expected = DBRepositoryClientException.class)
    public void testSaveException() throws DBRepositoryClientException {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");
        Client client1 = new Client("50001","name1","address1",2019);
        client1.setId(ID);
        assertTrue(clientRepository.save(client1).isPresent());
        Client client2 = new Client("50002","name2","address2",2020);
        client2.setId(ID);
        try {
            clientRepository.save(client2);
        }catch(DBRepositoryClientException e){
            clientRepository.delete(ID);
            throw e;
        }


    }

    /**
     * Tests whether a client can be removed from the repository.
     * (should result in no exceptions)
     *
     * @throws DBRepositoryClientException
     * if the deletion did not succeed
     */
    @Test
    public void testDelete() throws DBRepositoryClientException {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");
        Client client1 = new Client("50001","name1","address1",2019);
        client1.setId(ID);
        assertTrue(clientRepository.save(client1).isPresent());
        assertTrue(clientRepository.findOne(ID).isPresent());
        assertTrue(clientRepository.delete(ID).isPresent());
        assertFalse(clientRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether a client can be removed from the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryClientException
     * if the delete did not succeed
     */
    @Test(expected = DBRepositoryClientException.class)
    public void testDeleteException() throws DBRepositoryClientException {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");
        Client client1 = new Client("50001","name1","address1",2019);
        client1.setId(ID);
        clientRepository.delete(ID);


    }

    /**
     * Tests whether a client can be updated in the repository.
     * A new client is created and updated in the repository.
     * Then the old client is retrieved from the repository (should not be found)
     * and the new client is retrieved from the repository as well (should be found)
     *
     * @throws DBRepositoryClientException
     * if the update did not succeed
     */
    @Test
    public void testUpdate() throws DBRepositoryClientException {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");
        Client client1 = new Client("50001","name1","address1",2019);
        client1.setId(ID);
        assertTrue(clientRepository.save(client1).isPresent());
        assertTrue(clientRepository.findOne(ID).isPresent());
        Client client2 = new Client("50002","name2","address2",2020);
        client2.setId(ID);
        clientRepository.update(client2);
        assertTrue(clientRepository.findOne(ID).isPresent());
        clientRepository.delete(ID);
    }


    /**
     * Tests whether a client can be updated in the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryClientException
     * if the update did not succeed
     */
    @Test(expected = DBRepositoryClientException.class)
    public void testUpdateException() throws DBRepositoryClientException {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");
        Client client = new Client("50001","name1","address1",2019);
        client.setId(ID);
        clientRepository.update(client);


        ((DBRepositoryClient) clientRepository).dropTable();
    }

    /**
     * Tests whether we can get the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryClientException
     * if getTableName did not succeed
     */
    @Test
    public void testGetTableName() throws DBRepositoryClientException {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");
        assertSame("testTableName", ((DBRepositoryClient) clientRepository).getTableName());


    }

    /**
     * Tests whether we can set the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryClientException
     * if setTableName did not succeed
     */
    @Test
    public void testSetTableName() throws DBRepositoryClientException  {
        clientRepository = new DBRepositoryClient<>(clientValidator,"testTableName");

        ((DBRepositoryClient) clientRepository).setTableName("testTableName");
        assertSame("testTableName", ((DBRepositoryClient) clientRepository).getTableName());


    }


}