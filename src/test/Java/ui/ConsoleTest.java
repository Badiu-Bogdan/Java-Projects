package ui;

import domain.Adoption.Adoption;
import domain.Client.Client;
import domain.Pet.Pet;
import domain.Purchase.Purchase;
import domain.Toy.Toy;
import domain.validators.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRepository;
import repository.Repository;
import service.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleTest {

    private Validator<Pet> petValidator = new PetValidator();
    private Validator<Client> clientValidator = new ClientValidator();
    private Validator<Toy> toyValidator = new ToyValidator();
    private Validator<Adoption> adoptionValidator = new AdoptionValidator();
    private Validator<Purchase> purchaseValidator = new PurchaseValidator();

    Repository<Long, Pet> petRepository = new InMemoryRepository<>(petValidator);
    Repository<Long, Client> clientRepository = new InMemoryRepository<>(clientValidator);
    Repository<Long, Toy> toyRepository = new InMemoryRepository<>(toyValidator);
    Repository<Long, Adoption> adoptionRepository = new InMemoryRepository<>(adoptionValidator);
    Repository<Long, Purchase> purchaseRepository = new InMemoryRepository<>(purchaseValidator);

    private PetService petService = new PetService(petRepository);
    private ClientService clientService = new ClientService(clientRepository);
    private ToyService toyService = new ToyService(toyRepository);
    private AdoptionService adoptionService = new AdoptionService(adoptionRepository, clientRepository, petRepository);
    private StoreService storeService = new StoreService(purchaseRepository, clientRepository, toyRepository);


    private Console console = new Console();
    private BufferedReader bufferRead;
    private ByteArrayInputStream in;

    @BeforeEach
    void setUp() {
        console = new Console(petService, clientService, toyService, adoptionService, storeService);
        console.setUpMembers(Optional.of("memory"));
    }

    @AfterEach
    void tearDown() {
        console = null;
    }

    @Test
    void chooseRepo() {

        // choose 0
        in = new ByteArrayInputStream("0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.chooseRepo(bufferRead);
            fail();
        }catch(ConsoleException e) {}

        // choose 1
        in = new ByteArrayInputStream("1".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.chooseRepo(bufferRead);

        // choose 2
        in = new ByteArrayInputStream("2".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.chooseRepo(bufferRead);

        // choose 3
        in = new ByteArrayInputStream("3".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.chooseRepo(bufferRead);

        // choose 4
        in = new ByteArrayInputStream("4".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.chooseRepo(bufferRead);

        // choose something else. then choose 0
        in = new ByteArrayInputStream("500\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.chooseRepo(bufferRead);
            fail();
        }catch(ConsoleException e){}

        // choose something else (not number). then choose 0
        in = new ByteArrayInputStream("asd\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.chooseRepo(bufferRead);
            fail();
        }catch(ConsoleException e){}
    }

    @Test
    void runConsole() {
        in = new ByteArrayInputStream("0\n0\n".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.runConsole(bufferRead);

        in = new ByteArrayInputStream("2\n0\n".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.runConsole(bufferRead);

        in = new ByteArrayInputStream("3\n0\n".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.runConsole(bufferRead);

        in = new ByteArrayInputStream("4\n0\n".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.runConsole(bufferRead);
    }

    @Test
    void readUserInput() {

        in = new ByteArrayInputStream("1\n0\n".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.runConsole(bufferRead);

        // choose 0
        in = new ByteArrayInputStream("0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 1
        in = new ByteArrayInputStream("1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 2
        in = new ByteArrayInputStream("2\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 3
        in = new ByteArrayInputStream("3\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 4
        in = new ByteArrayInputStream("4\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 5
        in = new ByteArrayInputStream("5\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 6
        in = new ByteArrayInputStream("6\n1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 7
        in = new ByteArrayInputStream("7\n1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 8
        in = new ByteArrayInputStream("8\n1\n12345\nasd\n200\nmaterial\n2.99\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 9
        in = new ByteArrayInputStream("9\n1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 10
        in = new ByteArrayInputStream("10\n1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 11
        in = new ByteArrayInputStream("11\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 12
        in = new ByteArrayInputStream("12\n1\n\n0\n".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 13
        in = new ByteArrayInputStream("13\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 14
        in = new ByteArrayInputStream("14\n1\n12345\nasd\nasd\n2019\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 15
        in = new ByteArrayInputStream("15\n1\n12345\nasd\nasd\n2019\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 16
        in = new ByteArrayInputStream("16\n1\n12345\nasd\n200\nmaterial\n2.99\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 17
        in = new ByteArrayInputStream("17\n10\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 18
        in = new ByteArrayInputStream("18\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 19
        in = new ByteArrayInputStream("19\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 20
        in = new ByteArrayInputStream("20\n10\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 21
        in = new ByteArrayInputStream("21\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 22
        in = new ByteArrayInputStream("22\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 23
        in = new ByteArrayInputStream("23\n10\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 24
        in = new ByteArrayInputStream("24\n10\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 25
        in = new ByteArrayInputStream("25\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 26
        in = new ByteArrayInputStream("26\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 27
        in = new ByteArrayInputStream("27\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 28
        in = new ByteArrayInputStream("28\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 29
        in = new ByteArrayInputStream("29\nname\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 30
        in = new ByteArrayInputStream("30\nname\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);

        // choose 31
        in = new ByteArrayInputStream("31\nname\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.readUserInput(bufferRead);
    }

    @Test
    void addToy() {
    }

    @Test
    void adoptPet() {
    }

    @Test
    void buyToy() {
    }

    @Test
    void deletePet() {
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.deletePet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("2\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.deletePet(bufferRead);
            fail();
        }catch(IOException e){
            fail();
        }catch(ConsoleException e){}
    }

    @Test
    void deleteClient() {
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.deleteClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("2\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.deleteClient(bufferRead);
            fail();
        }catch(IOException e){
            fail();
        }catch(ConsoleException e){}
    }

    @Test
    void deleteToy() {
        in = new ByteArrayInputStream("1\n12345\nasd\n200\nmaterial\n2.99\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.deleteToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("2\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.deleteToy(bufferRead);
            fail();
        }catch(IOException e){
            fail();
        }catch(ConsoleException e){}
    }

    @Test
    void updatePet() {
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addPet(bufferRead);
        }catch(IOException e){
            fail();
        }
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.updatePet(bufferRead);
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void updateClient() {
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.updateClient(bufferRead);
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void updateToy() {
        in = new ByteArrayInputStream("1\n12345\nasd\n200\nmaterial\n2.99\n\n0".getBytes());

        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addToy(bufferRead);
        }catch(IOException e){
            fail();
        }
        in = new ByteArrayInputStream("1\n12345\nasd\n200\nmaterial\n2.99\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.updateToy(bufferRead);
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void getLastNAdoptions() {

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.adoptPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("-1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.getLastNAdoptions(bufferRead);
            fail();
        }catch(IOException e){
            fail();
        }catch(ConsoleException e){}
    }

    @Test
    void getMostAdoptedBreed() {
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.adoptPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.getMostAdoptedBreed();
    }

    @Test
    void getAverageAdoptedAge() {
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.adoptPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.getAverageAdoptedAge();
    }

    @Test
    void getClientOfTheYear() {
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.adoptPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("-2005\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.getClientOfTheYear(bufferRead);
            fail();
        }catch(IOException e){
            fail();
        }catch(ConsoleException e){}

        in = new ByteArrayInputStream("2021\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.getClientOfTheYear(bufferRead);
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void getMostRecentAdoption() {
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.adoptPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.getMostRecentAdoption();
    }

    @Test
    void getYoungestAdoptedPet() {
        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.adoptPet(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.getYoungestAdoptedPet();
    }

    @Test
    void getLastNPurchases() {
        in = new ByteArrayInputStream("1\n12345\nasd\n200\nmaterial\n2.99\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.buyToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("-1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.getLastNPurchases(bufferRead);
            fail();
        }catch(IOException e){
            fail();
        }catch(ConsoleException e){}
    }

    @Test
    void getNumberOfPurchasesForYear() {
        in = new ByteArrayInputStream("1\n12345\nasd\n200\nmaterial\n2.99\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.buyToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("-2005\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.getNumberOfPurchasesForYear(bufferRead);
            fail();
        }catch(IOException e){
            fail();
        }catch(ConsoleException e){}

        in = new ByteArrayInputStream("2019\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.getNumberOfPurchasesForYear(bufferRead);
        }catch(IOException e){
            fail();
        }
    }

    @Test
    void getMostPopularMaterial() {
        in = new ByteArrayInputStream("1\n12345\nasd\n200\nmaterial\n2.99\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.buyToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.getMostPopularMaterial();
    }

    @Test
    void getHeaviestPurchasedToy() {
        in = new ByteArrayInputStream("1\n12345\nasd\n200\nmaterial\n2.99\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.buyToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.getHeaviestPurchasedToy();
    }

    @Test
    void getMostExpensivePurchase() {
        in = new ByteArrayInputStream("1\n12345\nasd\n200\nmaterial\n2.99\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.buyToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.getMostExpensivePurchase();
    }

    @Test
    void getAveragePurchasedToysWeight() {
        in = new ByteArrayInputStream("1\n12345\nasd\n200\nmaterial\n2.99\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\nasd\nasd\n2019\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try {
            console.addClient(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("1\n12345\n1\n1\n\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        try{
            console.buyToy(bufferRead);
        }catch(IOException e){
            fail();
        }

        in = new ByteArrayInputStream("\n0".getBytes());
        bufferRead = new BufferedReader(new InputStreamReader(in));
        console.getAveragePurchasedToysWeight();
    }

}