package service;

import domain.Client.Client;
import domain.Purchase.Purchase;
import domain.Toy.Toy;
import domain.validators.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRepository;
import repository.Repository;
import service.exceptions.StoreServiceException;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class StoreServiceTest {

    private static final Long ID = new Long(1);

    private Validator<Client> clientValidator;
    private Repository<Long, Client> clientRepository;
    private ClientService clientService;
    private Validator<Toy> toyValidator;
    private Repository<Long, Toy> toyRepository;
    private ToyService toyService;
    private Validator<Purchase> purchaseValidator;
    private Repository<Long, Purchase> purchaseRepository;
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        clientValidator = new ClientValidator();
        clientRepository = new InMemoryRepository<>(clientValidator);
        clientService = new ClientService(clientRepository);
        toyValidator = new ToyValidator();
        toyRepository = new InMemoryRepository<>(toyValidator);
        toyService = new ToyService(toyRepository);
        purchaseValidator = new PurchaseValidator();
        purchaseRepository = new InMemoryRepository<>(purchaseValidator);
        storeService = new StoreService(purchaseRepository, clientRepository, toyRepository);
    }

    @AfterEach
    void tearDown() {
        clientValidator=null;
        clientRepository=null;
        clientService=null;
        toyValidator=null;
        toyRepository=null;
        toyService=null;
        purchaseValidator=null;
        purchaseRepository=null;
        storeService=null;
    }

    @Test
    void buyToy() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        Toy toy = new Toy("60001","name1",100,"material1",1.99);
        toy.setId(ID+1);
        try {
            storeService.buyToy(ID, "99999", client.getId(), toy.getId());
            fail();
        }catch(StoreServiceException e){}

        clientRepository.save(client);
        try {
            storeService.buyToy(ID, "99999", client.getId(), toy.getId());
            fail();
        }catch(StoreServiceException e){}

        toyRepository.save(toy);
        storeService.buyToy(ID, "99999", client.getId(), toy.getId());

        try{
            storeService.buyToy(ID, "19999", client.getId(), toy.getId());
            fail();
        }catch(StoreServiceException e){}
    }

    @Test
    void getAllPurchases() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        Toy toy1 = new Toy("60001","name1",100,"material1",1.99);
        toy1.setId(ID+1);
        toyRepository.save(toy1);
        Toy toy2 = new Toy("60002","name2",200,"material2",2.99);
        toy2.setId(ID+2);
        toyRepository.save(toy2);
        Toy toy3 = new Toy("60003","name3",300,"material3",3.99);
        toy3.setId(ID+3);
        toyRepository.save(toy3);

        storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
        storeService.buyToy(ID+1, "12346", client.getId(), toy2.getId());
        storeService.buyToy(ID+2, "12347", client.getId(), toy3.getId());
        assertEquals(3, storeService.getAllPurchases().size());
    }

    @Test
    void getLastNPurchases() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        Toy toy1 = new Toy("60001","name1",100,"material1",1.99);
        toy1.setId(ID+1);
        toyRepository.save(toy1);
        Toy toy2 = new Toy("60002","name2",200,"material2",2.99);
        toy2.setId(ID+2);
        toyRepository.save(toy2);
        Toy toy3 = new Toy("60003","name3",300,"material3",3.99);
        toy3.setId(ID+3);
        toyRepository.save(toy3);

        assertEquals(0, storeService.getLastNPurchases(100).size());

        storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
        assertEquals(1, storeService.getLastNPurchases(100).size());

        storeService.buyToy(ID+1, "12346", client.getId(), toy2.getId());
        assertEquals(2, storeService.getLastNPurchases(100).size());
        assertEquals(1, storeService.getLastNPurchases(1).size());

        storeService.buyToy(ID+2, "12347", client.getId(), toy3.getId());
        assertEquals(3, storeService.getLastNPurchases(100).size());
        assertEquals(2, storeService.getLastNPurchases(2).size());
        assertEquals(1, storeService.getLastNPurchases(1).size());

    }

    @Test
    void getNumberOfPurchasesForYear() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        Toy toy1 = new Toy("60001","name1",100,"material1",1.99);
        toy1.setId(ID+1);
        toyRepository.save(toy1);
        Toy toy2 = new Toy("60002","name2",200,"material2",2.99);
        toy2.setId(ID+2);
        toyRepository.save(toy2);
        Toy toy3 = new Toy("60003","name3",300,"material3",3.99);
        toy3.setId(ID+3);
        toyRepository.save(toy3);

        try{
            storeService.getNumberOfPurchasesForYear(2005);
            fail();
        }catch(StoreServiceException e){}

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
        assertEquals(1, storeService.getNumberOfPurchasesForYear(currentYear));
        storeService.buyToy(ID+1, "12346", client.getId(), toy2.getId());
        assertEquals(2, storeService.getNumberOfPurchasesForYear(currentYear));
        storeService.buyToy(ID+2, "12347", client.getId(), toy3.getId());
        assertEquals(3, storeService.getNumberOfPurchasesForYear(currentYear));
    }

    @Test
    void getMostPopularMaterial() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        Toy toy1 = new Toy("60001","name1",100,"plastic",1.99);
        toy1.setId(ID+1);
        toyRepository.save(toy1);
        Toy toy2 = new Toy("60002","name2",200,"wood",2.99);
        toy2.setId(ID+2);
        toyRepository.save(toy2);
        Toy toy3 = new Toy("60003","name3",300,"plastic",3.99);
        toy3.setId(ID+3);
        toyRepository.save(toy3);

        storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
        storeService.buyToy(ID+1, "12346", client.getId(), toy2.getId());
        storeService.buyToy(ID+2, "12347", client.getId(), toy3.getId());
        assertEquals("plastic", storeService.getMostPopularMaterial());
    }

    @Test
    void getHeaviestPurchasedToy() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        int weight1 = 200;
        Toy toy1 = new Toy("60001","name1",weight1,"plastic",1.99);
        toy1.setId(ID+1);
        toyRepository.save(toy1);
        int weight2 = 300;
        Toy toy2 = new Toy("60002","name2",weight2,"wood",2.99);
        toy2.setId(ID+2);
        toyRepository.save(toy2);
        int weight3 = 400;
        Toy toy3 = new Toy("60003","name3",weight3,"plastic",3.99);
        toy3.setId(ID+3);
        toyRepository.save(toy3);

        try{
            storeService.getHeaviestPurchasedToy();
            fail();
        }catch(StoreServiceException e){}

        storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
        storeService.buyToy(ID+1, "12346", client.getId(), toy2.getId());
        storeService.buyToy(ID+2, "12347", client.getId(), toy3.getId());
        assertEquals(toy3, storeService.getHeaviestPurchasedToy());
    }

    @Test
    void getMostExpensivePurchase() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        double price1 = 2.99;
        Toy toy1 = new Toy("60001","name1",100,"plastic",price1);
        toy1.setId(ID+1);
        toyRepository.save(toy1);
        double price2 = 6.99;
        Toy toy2 = new Toy("60002","name2",200,"wood",price2);
        toy2.setId(ID+2);
        toyRepository.save(toy2);
        double price3 = 13.99;
        Toy toy3 = new Toy("60003","name3",300,"plastic",price3);
        toy3.setId(ID+3);
        toyRepository.save(toy3);

        try{
            storeService.getMostExpensivePurchase();
            fail();
        }catch(StoreServiceException e){}

        storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
        storeService.buyToy(ID+1, "12346", client.getId(), toy2.getId());
        storeService.buyToy(ID+2, "12347", client.getId(), toy3.getId());
        Purchase purchase = storeService.getMostExpensivePurchase();
        assertEquals(client.getId(), purchase.getClientId());
        assertEquals(toy3.getId(), purchase.getToyId());
    }

    @Test
    void getAveragePurchasedToysWeight() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        int weight1 = 200;
        Toy toy1 = new Toy("60001","name1",weight1,"plastic",1.99);
        toy1.setId(ID+1);
        toyRepository.save(toy1);
        int weight2 = 300;
        Toy toy2 = new Toy("60002","name2",weight2,"wood",2.99);
        toy2.setId(ID+2);
        toyRepository.save(toy2);
        int weight3 = 400;
        Toy toy3 = new Toy("60003","name3",weight3,"plastic",3.99);
        toy3.setId(ID+3);
        toyRepository.save(toy3);

        try{
            storeService.getAveragePurchasedToysWeight();
            fail();
        }catch(StoreServiceException e){}

        storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
        storeService.buyToy(ID+1, "12346", client.getId(), toy2.getId());
        storeService.buyToy(ID+2, "12347", client.getId(), toy3.getId());
        int expectedAverage = (weight1 + weight2 + weight3) / 3;
        assertEquals(expectedAverage, storeService.getAveragePurchasedToysWeight());
    }

    @Test
    void deletePurchasesForClient() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        Toy toy1 = new Toy("60001","name1",100,"material1",1.99);
        toy1.setId(ID+1);
        toyRepository.save(toy1);

        storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
        assertEquals(1, storeService.getAllPurchases().size());

        try{
            storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
            fail();
        }catch(StoreServiceException e){}

        storeService.deletePurchasesForClient(client.getId());
        assertEquals(0, storeService.getAllPurchases().size());
    }

    @Test
    void deletePurchasesForToy() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        Toy toy1 = new Toy("60001","name1",100,"material1",1.99);
        toy1.setId(ID+1);
        toyRepository.save(toy1);

        storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
        assertEquals(1, storeService.getAllPurchases().size());

        try{
            storeService.buyToy(ID, "12345", client.getId(), toy1.getId());
            fail();
        }catch(StoreServiceException e){}

        storeService.deletePurchasesForToy(toy1.getId());
        assertEquals(0, storeService.getAllPurchases().size());
    }
}
