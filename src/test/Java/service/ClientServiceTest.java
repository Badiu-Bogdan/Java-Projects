package service;

import domain.Client.Client;
import domain.validators.ClientValidator;
import domain.validators.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRepository;
import repository.Repository;
import service.exceptions.ClientServiceException;

import static org.junit.Assert.*;

public class ClientServiceTest {

    private static final Long ID = new Long(1);

    private Validator<Client> clientValidator;
    private Repository<Long, Client> clientRepository;
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientValidator = new ClientValidator();
        clientRepository = new InMemoryRepository<>(clientValidator);
        clientService = new ClientService(clientRepository);
    }

    @AfterEach
    void tearDown() {
        clientValidator=null;
        clientRepository=null;
        clientService=null;
    }

    @Test
    void addClient() {
        clientRepository = new InMemoryRepository<>(clientValidator);
        Client client = new Client("921","name1","breed1",2019);
        client.setId(ID);
        clientService.addClient(client);
        assertTrue(clientService.getAllClients().contains(client));
        client.setId(null);
        try{
            clientService.addClient(client);
            fail();
        }catch(ClientServiceException e){

        }
    }

    @Test
    void getAllClients() {
    }

    @Test
    void filterClientsByName() {
        clientRepository = new InMemoryRepository<>(clientValidator);
        Client client = new Client("921","name1","breed1",2019);
        client.setId(ID);
        clientService.addClient(client);
        assertTrue(clientService.filterClientsByName("name").contains(client));
        assertFalse(clientService.filterClientsByName("asd").contains(client));
    }

    @Test
    void deleteClient() {
        clientRepository = new InMemoryRepository<>(clientValidator);
        Client client = new Client("921","name1","breed1",2019);

        try{
            clientService.deleteClient(client.getId());
            fail();
        }catch(NullPointerException e){

        }

        client.setId(ID);
        clientService.addClient(client);
        clientService.deleteClient(client.getId());
        assertFalse(clientService.getAllClients().contains(client));
        assertTrue(clientService.getAllClients().isEmpty());

        try{
            clientService.deleteClient(ID + 1);
            fail();
        }catch(ClientServiceException e){

        }
    }

    @Test
    void updateClient() {
        clientRepository = new InMemoryRepository<>(clientValidator);
        Client client = new Client("921","name1","breed1",2019);

        try{
            clientService.updateClient(client.getId(), null);
            fail();
        }catch(NullPointerException e){

        }

        client.setId(ID);
        clientService.addClient(client);
        client.setName("new");
        client.setAddress("new");
        clientService.updateClient(client.getId(), client);
        assertTrue(clientService.getAllClients().contains(client));
        assertFalse(clientService.getAllClients().isEmpty());

        try{
            clientService.updateClient(ID + 1, client);
            fail();
        }catch(ClientServiceException e){

        }
    }
}
