package ui;

import domain.Adoption.Adoption;
import domain.Client.Client;
import domain.Pet.Pet;
import domain.Purchase.Purchase;
import domain.Toy.Toy;
import domain.validators.*;
import domain.validators.exceptions.*;
import repository.InMemoryRepository;
import repository.Repository;
import repository.XML.*;
import repository.file.*;
import service.*;
import service.exceptions.ServiceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Console {

    private PetService petService;
    private ClientService clientService;
    private ToyService toyService;
    private AdoptionService adoptionService;
    private StoreService storeService;

    private Validator<Pet> petValidator;
    private Validator<Client> clientValidator;
    private Validator<Toy> toyValidator;
    private Validator<Adoption> adoptionValidator;
    private Validator<Purchase> purchaseValidator;

    public Console(){

    }

    public Console(PetService petService, ClientService clientService, ToyService toyService, AdoptionService adoptionService, StoreService storeService){
        this.petService = petService;
        this.clientService = clientService;
        this.toyService = toyService;
        this.adoptionService = adoptionService;
        this.storeService = storeService;
    }

    /**
     * Print console messages for the repository choice
     *
     */
    public void printRepoChoice(){
        System.out.println("----- Choose the type of repository -----");
        System.out.println("0. Exit");
        System.out.println("1. In memory");
        System.out.println("2. File based");
        System.out.println("3. XML based");
        System.out.println("4. JDBC");
    }

    /**
     * Print console messages for the menu option
     *
     */
    public void printMenu(){
        System.out.println("---------- Menu ----------");
        System.out.println("---- *Query data* ----");
        System.out.println("1. See all Pets");
        System.out.println("2. See all Clients");
        System.out.println("3. See all Toys");
        System.out.println("4. See all Adoptions");
        System.out.println("5. See all Purchases");
        System.out.println("---- *Modify data* ----");
        System.out.println("6. Add a new Pet");
        System.out.println("7. Add a new Client");
        System.out.println("8. Add a new Toy");
        System.out.println("9. Adopt a Pet");
        System.out.println("10. Buy a Toy");
        System.out.println("11. Delete a Pet");
        System.out.println("12. Delete a Client");
        System.out.println("13. Delete a Toy");
        System.out.println("14. Update a Pet");
        System.out.println("15. Update a Client");
        System.out.println("16. Update a Toy");
        System.out.println("---- *Filter data* ----");
        System.out.println("17. Get the last N adoptions");
        System.out.println("18. Get the most adopted breed");
        System.out.println("19. Get the average adoption age");
        System.out.println("20. Get the client of the year");
        System.out.println("21. Get the most recent adoption");
        System.out.println("22. Get the youngest adopted pet");
        System.out.println("23. Get the last N purchases");
        System.out.println("24. Get the number of purchases for a given year");
        System.out.println("25. Get the most popular material");
        System.out.println("26. Get the heaviest purchased toy");
        System.out.println("27. Get the most expensive purchase");
        System.out.println("28. Get the average purchased toy's weight");
        System.out.println("29. Get pets by name");
        System.out.println("30. Get clients by name");
        System.out.println("31. Get toys by name");
        System.out.println("----- 0. Exit -----");
    }

    /**
     * Reads the console user input regarding the type of repository
     * to be used in the application
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @returns Optional<String>
     *                  Optional with the chosen type of repository, null optional if none
     */
    public Optional<String> chooseRepo(BufferedReader bufferRead){
        while(true){
            this.printRepoChoice();
            try {
                System.out.print(">>");
                int option = Integer.parseInt(bufferRead.readLine());
                Optional.of(option).filter(v -> v > 0).orElseThrow(() ->
                        new ConsoleException("Goodbye.")
                );
                switch(option){
                    case 1:
                        return Optional.of("memory");
                    case 2:
                        return Optional.of("file");
                    case 3:
                        return Optional.of("xml");
                    case 4:
                        return Optional.of("database");
                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (IOException ex) {
                System.out.println("There was an error reading the repository option. Try again.");
            } catch(NumberFormatException nfe){
                System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * Main function of the Console that initializes the UI process
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     */
    public void runConsole(BufferedReader bufferRead){

        try {
            Optional<String> typeOfRepo = this.chooseRepo(bufferRead);
            this.setUpMembers(typeOfRepo);
            this.readUserInput(bufferRead);

        }catch(ConsoleException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Set up the validators, repositories and services
     *
     * @param typeOfRepo: Optional<String> type of repository
     */
    public void setUpMembers(Optional<String> typeOfRepo){
        // initialize validators
        this.petValidator = new PetValidator();
        this.clientValidator = new ClientValidator();
        this.toyValidator = new ToyValidator();
        this.adoptionValidator = new AdoptionValidator();
        this.purchaseValidator = new PurchaseValidator();

        // declare repositories
        Repository<Long, Pet> petRepository = null;
        Repository<Long, Client> clientRepository = null;
        Repository<Long, Toy> toyRepository = null;
        Repository<Long, Adoption> adoptionRepository = null;
        Repository<Long, Purchase> purchaseRepository = null;

        // assign proper repositories based on the type of chosen repository
        switch(typeOfRepo.get()){
            case "memory":
                petRepository = new InMemoryRepository<>(petValidator);
                clientRepository = new InMemoryRepository<>(clientValidator);
                toyRepository = new InMemoryRepository<>(toyValidator);
                adoptionRepository = new InMemoryRepository<>(adoptionValidator);
                purchaseRepository = new InMemoryRepository<>(purchaseValidator);
                break;
            case "xml":
                petRepository = new XMLRepositoryPet<>(petValidator, "pets");
                clientRepository = new XMLRepositoryClient<>(clientValidator, "clients");
                toyRepository = new XMLRepositoryToy<>(toyValidator, "toys");
                adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "adoptions");
                purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "purchases");
                break;
            case "file":
                petRepository = new FileRepositoryPet<>(petValidator, "data/file/pets.csv");
                clientRepository = new FileRepositoryClient<>(clientValidator, "data/file/clients.csv");
                toyRepository = new FileRepositoryToy<>(toyValidator, "data/file/toys.csv");
                adoptionRepository = new FileRepositoryAdoption<>(adoptionValidator, "data/file/adoptions.csv");
                purchaseRepository = new FileRepositoryStore<>(purchaseValidator, "data/file/purchases.csv");
                break;
            default:
                System.out.println("Should not be here (yet).");
                break;

        }

        // initialize the services
        this.petService = new PetService(petRepository);
        this.clientService = new ClientService(clientRepository);
        this.toyService = new ToyService(toyRepository);
        this.adoptionService = new AdoptionService(adoptionRepository, clientRepository, petRepository);
        this.storeService = new StoreService(purchaseRepository, clientRepository, toyRepository);
    }

    /**
     * Reads the user input and takes decisions based on it regarding the features
     * the application provides
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     */
    public void readUserInput(BufferedReader bufferRead){
        boolean running = true;
        while(running){
            this.printMenu();
            try{
                System.out.print(">> ");
                String option = bufferRead.readLine();

                switch(option){
                    case "0":
                        running = false;
                        System.out.println("Goodbye!");
                        return;
                    case "1": // 1. See all Pets
                        this.petService.getAllPets().forEach(pet -> System.out.println(pet.toString()));
                        break;
                    case "2": // 2. See all Clients
                        this.clientService.getAllClients().forEach(client -> System.out.println(client.toString()));
                        break;
                    case "3": // 3. See all Toys
                        this.toyService.getAllToys().forEach(toy -> System.out.println(toy.toString()));
                        break;
                    case "4": // 4. See all Adoptions
                        this.adoptionService.getAllAdoptions().forEach(adoption -> System.out.println(adoption.toString()));
                        break;
                    case "5": // 5. See all purchases
                        this.storeService.getAllPurchases().forEach(purchase -> System.out.println(purchase.toString()));
                        break;
                    case "6": // 6. Add a new Pet
                        this.addPet(bufferRead);
                        break;
                    case "7": // 7. Add a new Client
                        this.addClient(bufferRead);
                        break;
                    case "8": // 8. Add a new Toy
                        this.addToy(bufferRead);
                        break;
                    case "9": // 9. Adopt a Pet
                        this.adoptPet(bufferRead);
                        break;
                    case "10": // 10. Buy a Toy
                        this.buyToy(bufferRead);
                        break;
                    case "11": // 11. Delete a Pet
                        this.deletePet(bufferRead);
                        break;
                    case "12": // 12. Delete a Client
                        this.deleteClient(bufferRead);
                        break;
                    case "13": // 13. Delete a Toy
                        this.deleteToy(bufferRead);
                        break;
                    case "14": // 14. Update a Pet
                        this.updatePet(bufferRead);
                        break;
                    case "15": // 15. Update a Client
                        this.updateClient(bufferRead);
                        break;
                    case "16": // 16. Update a Toy
                        this.updateToy(bufferRead);
                        break;
                    case "17": // 17. Get the last N adoptions
                        this.getLastNAdoptions(bufferRead);
                        break;
                    case "18": // 18. Get the most adopted breed
                        this.getMostAdoptedBreed();
                        break;
                    case "19": // 19. Get the average adoption age
                        this.getAverageAdoptedAge();
                        break;
                    case "20": // 20. Get the client of the year
                        this.getClientOfTheYear(bufferRead);
                        break;
                    case "21": // 21. Get the most recent adoption
                        this.getMostRecentAdoption();
                        break;
                    case "22": // 22. Get the youngest adopted pet
                        this.getYoungestAdoptedPet();
                        break;
                    case "23": // 23. Get the last N purchases
                        this.getLastNPurchases(bufferRead);
                        break;
                    case "24": // 24. Get the number of purchases for a given year
                        this.getNumberOfPurchasesForYear(bufferRead);
                        break;
                    case "25": // 25. Get the most popular material
                        this.getMostPopularMaterial();
                        break;
                    case "26": // 26. Get the heaviest purchased toy
                        this.getHeaviestPurchasedToy();
                        break;
                    case "27": // 27. Get the most expensive purchase
                        this.getMostExpensivePurchase();
                        break;
                    case "28": // 28. Get the average purchased toy's weight
                        this.getAveragePurchasedToysWeight();
                        break;
                    case "29": // 29. Get pets by name
                        this.getPetsByName(bufferRead);
                        break;
                    case "30": // 30. Get clients by name
                        this.getClientsByName(bufferRead);
                        break;
                    case "31": // 31. Get toys by name
                        this.getToysByName(bufferRead);
                        break;
                    default:
                        System.out.println("Invalid input");
                        break;
                }

                System.out.print("continue ... (press enter) ");
                bufferRead.read();
            }catch (IOException | ConsoleException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Read data for constructing a Pet
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */
    public void addPet(BufferedReader bufferRead) throws IOException, ValidatorException {
        try {
            // read input
            System.out.print("ID: ");
            long id = Long.parseLong(bufferRead.readLine());
            System.out.print("Serial Number: ");
            String serialNumber = bufferRead.readLine();
            System.out.print("Name: ");
            String name = bufferRead.readLine();
            System.out.print("Breed: ");
            String breed = bufferRead.readLine();
            System.out.print("Birth date (year): ");
            int birthDate = Integer.parseInt(bufferRead.readLine());

            // add pet to service
            Pet pet = new Pet(serialNumber, name, breed, birthDate);
            pet.setId(id);

            // validate the data
            this.petValidator.validate(pet);

            this.petService.addPet(pet);
        }catch(ValidatorException | NumberFormatException | ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
        System.out.println("+Pet added successfully");
    }

    /**
     * Read data for constructing a Client
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */
    public void addClient(BufferedReader bufferRead) throws IOException, ValidatorException {
        // read input
        System.out.print("ID: ");
        long id = Long.parseLong(bufferRead.readLine());
        System.out.print("Serial Number: ");
        String serialNumber = bufferRead.readLine();
        System.out.print("Name: ");
        String name = bufferRead.readLine();
        System.out.print("Address: ");
        String address = bufferRead.readLine();
        System.out.print("Year Of Registration (year): ");
        int yearOfRegistration = Integer.parseInt(bufferRead.readLine());

        // add client to service
        Client client = new Client(serialNumber, name, address, yearOfRegistration);
        client.setId(id);

        try {
            // validate the data
            this.clientValidator.validate(client);

            this.clientService.addClient(client);
        }catch(ValidatorException | NumberFormatException | ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
        System.out.println("+Client added successfully");
    }

    /**
     * Read data for constructing a Toy
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */
    public void addToy(BufferedReader bufferRead) throws IOException, ValidatorException {
        // read input
        System.out.print("ID: ");
        long id = Long.parseLong(bufferRead.readLine());
        System.out.print("Serial Number: ");
        String serialNumber = bufferRead.readLine();
        System.out.print("Name: ");
        String name = bufferRead.readLine();
        System.out.print("Weight (grams): ");
        int weight = Integer.parseInt(bufferRead.readLine());
        System.out.print("Material: ");
        String material = bufferRead.readLine();
        System.out.print("Price: ");
        double price = Double.parseDouble(bufferRead.readLine());

        // add toy to service
        Toy toy = new Toy(serialNumber, name, weight, material, price);
        toy.setId(id);

        try {
            // validate the data
            this.toyValidator.validate(toy);

            this.toyService.addToy(toy);
        }catch(ValidatorException | NumberFormatException | ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
        System.out.println("+Toy added successfully");
    }

    /**
     * Read data for constructing an Adoption
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     * @throws ConsoleException if invalid client or pet ids were given
     */
    public void adoptPet(BufferedReader bufferRead) throws IOException, ConsoleException {
        // read input
        System.out.print("ID: ");
        long id = Long.parseLong(bufferRead.readLine());
        System.out.print("Serial Number: ");
        String serialNumber = bufferRead.readLine();
        System.out.print("ClientId: ");
        long clientId = Long.parseLong(bufferRead.readLine());
        System.out.print("PetId: ");
        long petId = Long.parseLong(bufferRead.readLine());

        Optional.of(clientId).filter(v -> v >= 0).orElseThrow(() ->
                new ConsoleException("-Error: Invalid ClientId value.")
        );
        Optional.of(petId).filter(v -> v >= 0).orElseThrow(() ->
                new ConsoleException("-Error: Invalid PetId value.")
        );

        try {
            this.adoptionService.adoptPet(id, serialNumber, clientId, petId);
            System.out.println("+Adoption made successfully");
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Read data for constructing a Purchase
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     * @throws ConsoleException if invalid client or toy ids were given
     */
    public void buyToy(BufferedReader bufferRead) throws IOException, ConsoleException {
        // read input
        System.out.print("ID: ");
        long id = Long.parseLong(bufferRead.readLine());
        System.out.print("Serial Number: ");
        String serialNumber = bufferRead.readLine();
        System.out.print("ClientId: ");
        long clientId = Long.parseLong(bufferRead.readLine());
        System.out.print("ToyId: ");
        long toyId = Long.parseLong(bufferRead.readLine());

        Optional.of(clientId).filter(v -> v >= 0).orElseThrow(() ->
                new ConsoleException("-Error: Invalid ClientId value.")
        );
        Optional.of(toyId).filter(v -> v >= 0).orElseThrow(() ->
                new ConsoleException("-Error: Invalid ToyId value.")
        );

        try {
            this.storeService.buyToy(id, serialNumber, clientId, toyId);
            System.out.println("+Purchase made successfully");
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Read data for deleting a Pet
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */
    public void deletePet(BufferedReader bufferRead) throws IOException{
        try {
            // read input
            System.out.print("ID: ");
            long id = Long.parseLong(bufferRead.readLine());

            petService.deletePet(id);

            adoptionService.deleteAdoptionsForPet(id);

        }catch(ValidatorException | NumberFormatException | ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
        System.out.println("+Pet deleted successfully");
    }

    /**
     * Read data for deleting a Client
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */
    public void deleteClient(BufferedReader bufferRead) throws IOException{
        try {
            // read input
            System.out.print("ID: ");
            long id = Long.parseLong(bufferRead.readLine());

            clientService.deleteClient(id);

            adoptionService.deleteAdoptionsForClient(id);
            storeService.deletePurchasesForClient(id);

        }catch(ValidatorException | NumberFormatException | ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
        System.out.println("+Client deleted successfully");
    }

    /**
     * Read data for deleting a Toy
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */
    public void deleteToy(BufferedReader bufferRead) throws IOException{
        try {
            // read input
            System.out.print("ID: ");
            long id = Long.parseLong(bufferRead.readLine());

            toyService.deleteToy(id);

            storeService.deletePurchasesForToy(id);

        }catch(ValidatorException | NumberFormatException | ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
        System.out.println("+Toy deleted successfully");
    }

    /**
     * Read data for updating a Pet
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */

    public void updatePet(BufferedReader bufferRead) throws IOException{
        try {
            // read input
            System.out.print("ID (of the pet to be updated): ");
            long id = Long.parseLong(bufferRead.readLine());

            System.out.println("(Data about the new Pet)");
            System.out.print("Serial Number: ");
            String serialNumber = bufferRead.readLine();
            System.out.print("Name: ");
            String name = bufferRead.readLine();
            System.out.print("Breed: ");
            String breed = bufferRead.readLine();
            System.out.print("Birth date (year): ");
            int birthDate = Integer.parseInt(bufferRead.readLine());

            Pet pet = new Pet(serialNumber, name, breed, birthDate);
            pet.setId(id);

            // validate the data
            this.petValidator.validate(pet);

            this.petService.updatePet(id, pet);

        }catch(ValidatorException | NumberFormatException | ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
        System.out.println("+Pet updated successfully");
    }

    /**
     * Read data for updating a Client
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */

    public void updateClient(BufferedReader bufferRead) throws IOException{
        try {
            // read input
            System.out.print("ID (of the client to be updated): ");
            long id = Long.parseLong(bufferRead.readLine());

            System.out.println("(Data about the new Client)");
            System.out.print("Serial Number: ");
            String serialNumber = bufferRead.readLine();
            System.out.print("Name: ");
            String name = bufferRead.readLine();
            System.out.print("Address: ");
            String address = bufferRead.readLine();
            System.out.print("Year Of Registration (year): ");
            int yearOfRegistration = Integer.parseInt(bufferRead.readLine());

            Client client = new Client(serialNumber, name, address, yearOfRegistration);
            client.setId(id);

            // validate the data
            this.clientValidator.validate(client);

            this.clientService.updateClient(id, client);

        }catch(ValidatorException | NumberFormatException | ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
        System.out.println("+Client updated successfully");
    }

    /**
     * Read data for updating a Toy
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */

    public void updateToy(BufferedReader bufferRead) throws IOException{
        try {
            // read input
            System.out.print("ID (of the toy to be updated): ");
            long id = Long.parseLong(bufferRead.readLine());

            System.out.println("(Data about the new Toy)");
            System.out.print("Serial Number: ");
            String serialNumber = bufferRead.readLine();
            System.out.print("Name: ");
            String name = bufferRead.readLine();
            System.out.print("Weight (grams): ");
            int weight = Integer.parseInt(bufferRead.readLine());
            System.out.print("Material: ");
            String material = bufferRead.readLine();
            System.out.print("Price: ");
            double price = Double.parseDouble(bufferRead.readLine());

            // add pet to service
            Toy toy = new Toy(serialNumber, name, weight, material, price);
            toy.setId(id);

            // validate the data
            this.toyValidator.validate(toy);

            this.toyService.updateToy(id, toy);

        }catch(ValidatorException | NumberFormatException | ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
        System.out.println("+Toy updated successfully");
    }

    /**
     * Handle the call for getLastNAdoptions methods from AdoptionsService
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     * @throws ConsoleException if the given N value is less than 0
     */
    public void getLastNAdoptions(BufferedReader bufferRead) throws IOException, ConsoleException {
        // read input
        System.out.print("N: ");
        int n = Integer.parseInt(bufferRead.readLine());

        Optional.of(n).filter(v -> v >= 0).orElseThrow(() ->
                new ConsoleException("Error: Invalid N value.")
        );

        try {
            List<Adoption> adoptions = this.adoptionService.getLastNAdoptions(n);
            System.out.println(adoptions.size() + " adoptions were found.");
            adoptions.forEach(adoption -> System.out.println(adoption.toString()));
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Handle the call for getMostAdoptedBreed methods from AdoptionsService
     *
     */
    public void getMostAdoptedBreed() {
        try {
            String breed = this.adoptionService.mostAdoptedBreed();
            System.out.println("The most adopted breed is " + breed + ".");
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Handle the call for getAverageAdoptionAge methods from AdoptionsService
     *
     */
    public void getAverageAdoptedAge() {
        try {
            long age = this.adoptionService.getAverageAdoptedAge();
            System.out.println("The average adoption age is " + age + ".");
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Handle the call for getClientOfTheYear methods from StoreService
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     * @throws ConsoleException if the year value is invalid
     */
    public void getClientOfTheYear(BufferedReader bufferRead) throws IOException, ConsoleException {
        System.out.print("year: ");
        int year = Integer.parseInt(bufferRead.readLine());

        Optional.of(year).filter(v -> v >= 0).orElseThrow(() ->
                new ConsoleException("Error: Invalid year value.")
        );

        try {
            Client client = this.adoptionService.getClientOfTheYear(year);
            System.out.println("The client of the year is " + client.toString() + ".");
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Handle the call for getMostRecentAdoption methods from AdoptionService
     *
     */
    public void getMostRecentAdoption() {
        try {
            Adoption adoption = this.adoptionService.getMostRecentAdoption();
            System.out.println("The most recent adoption is " + adoption.toString() + ".");
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Handle the call for getYoungestAdoptedPet methods from AdoptionService
     *
     */
    public void getYoungestAdoptedPet() {
        try {
            Optional<Pet> pet = this.adoptionService.getYoungestAdoptedPet();
            System.out.println("The youngest adopted pet is " + pet.get().toString() + ".");
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Handle the call for getLastNPurchases methods from AdoptionService
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     * @throws ConsoleException if the given N value is less than 0
     */
    public void getLastNPurchases(BufferedReader bufferRead) throws IOException, ConsoleException {
        // read input
        System.out.print("N: ");
        int n = Integer.parseInt(bufferRead.readLine());

        Optional.of(n).filter(v -> v >= 0).orElseThrow(() ->
                new ConsoleException("Error: Invalid N value.")
        );

        try {
            List<Purchase> purchases = this.storeService.getLastNPurchases(n);
            System.out.println(purchases.size() + " purchases were found.");
            purchases.forEach(purchase -> System.out.println(purchase.toString()));
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Handle the call for getNumberOfPurchasesForYear methods from StoreService
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     * @throws ConsoleException if the given year value is less than 0
     */
    public void getNumberOfPurchasesForYear(BufferedReader bufferRead) throws IOException, ConsoleException {
        // read input
        System.out.print("Year: ");
        int year = Integer.parseInt(bufferRead.readLine());

        Optional.of(year).filter(v -> v >= 0).orElseThrow(() ->
                new ConsoleException("Error: Invalid year value.")
        );

        try {
            long number = this.storeService.getNumberOfPurchasesForYear(year);
            System.out.println(number + " purchases were found in " + year + ".");
        }catch(ServiceException e){
            throw new ConsoleException(e.getMessage());
        }
    }

    /**
     * Handle the call for getMostPopularMaterial methods from StoreService
     *
     */
    public void getMostPopularMaterial() {
        try {
            String material = this.storeService.getMostPopularMaterial();
            System.out.println("The most popular material is " + material + ".");
        }catch(ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handle the call for getHeaviestPurchasedToy methods from StoreService
     *
     */
    public void getHeaviestPurchasedToy() {
        try {
            Toy toy = this.storeService.getHeaviestPurchasedToy();
            System.out.println("The heaviest purchased toy is " + toy.toString() + ".");
        }catch(ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handle the call for getMostExpensivePurchase methods from StoreService
     *
     */
    public void getMostExpensivePurchase() {
        try {
            Purchase purchase = this.storeService.getMostExpensivePurchase();
            System.out.println("The most expensive purchase is " + purchase.toString() + ".");
        }catch(ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handle the call for getAveragePurchasedToysWeight methods from StoreService
     *
     */
    public void getAveragePurchasedToysWeight() {
        try {
            long weight = this.storeService.getAveragePurchasedToysWeight();
            System.out.println("The average purchased toy weight is " + weight + ".");
        }catch(ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handle the call for getPetsByName methods from PetService
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */
    public void getPetsByName(BufferedReader bufferRead) throws IOException {
        // read input
        System.out.print("Name: ");
        String name = bufferRead.readLine();
        Set<Pet> pets = this.petService.filterPetsByName(name);
        System.out.println(pets.size() + " pets were found.");
        pets.forEach(pet -> System.out.println(pet.toString()));
    }

    /**
     * Handle the call for getClientsByName methods from ClientService
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */
    public void getClientsByName(BufferedReader bufferRead) throws IOException {
        // read input
        System.out.print("Name: ");
        String name = bufferRead.readLine();
        Set<Client> clients = this.clientService.filterClientsByName(name);
        System.out.println(clients.size() + " clients were found.");
        clients.forEach(client -> System.out.println(client.toString()));
    }

    /**
     * Handle the call for getToysByName methods from ToyService
     *
     * @param bufferRead: BufferedReader buffer for reading the input
     * @throws IOException
     *         if the buffered reader fails for some reason
     */
    public void getToysByName(BufferedReader bufferRead) throws IOException {
        // read input
        System.out.print("Name: ");
        String name = bufferRead.readLine();
        Set<Toy> toys = this.toyService.filterToysByName(name);
        System.out.println(toys.size() + " toys were found.");
        toys.forEach(toy -> System.out.println(toy.toString()));
    }

}
