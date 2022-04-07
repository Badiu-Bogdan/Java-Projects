package service;

import domain.Client.Client;
import domain.Purchase.Purchase;
import domain.Purchase.PurchaseYearComparator;
import domain.Toy.Toy;
import domain.Toy.ToyPriceComparator;
import domain.Toy.ToyWeightComparator;
import domain.validators.exceptions.ValidatorException;
import repository.RepositoryException;
import repository.Repository;
import service.exceptions.StoreServiceException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StoreService {
    private final Repository<Long, Purchase> purchaseRepository;
    private final Repository<Long, Client> clientRepository;
    private final Repository<Long, Toy> toyRepository;

    public StoreService(Repository<Long, Purchase> purchaseRepository, Repository<Long, Client> clientRepository, Repository<Long, Toy> toyRepository) {
        this.purchaseRepository = purchaseRepository;
        this.clientRepository = clientRepository;
        this.toyRepository = toyRepository;
    }

    /**
     * Buy a toy given a clientId and a toyId
     *
     * @param clientId: Long id of the client
     *        toyId: Long id of the toy to be bought
     * @throws StoreServiceException
     *          if the given clientId or toyId does not correspond to a Client or Toy from their repositories
     */
    public void buyToy(Long id, String serialNumber, Long clientId, Long toyId) throws StoreServiceException {

        clientRepository.findOne(clientId).orElseThrow(() ->
                new StoreServiceException("Could not find client with the given clientId.")
        );
        toyRepository.findOne(toyId).orElseThrow(() ->
                new StoreServiceException("Could not find toy with the given toyId.")
        );
        // check if toy is available for purchase
        List<Purchase> purchases = StreamSupport
                .stream(purchaseRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // filter purchases containing the toyId
        long cnt = purchases.stream().filter(purc -> purc.getToyId() == toyId).count();
        Optional.of(cnt).filter(c -> c == 0).orElseThrow(() ->
                new StoreServiceException("Toy was already purchased.")
        );

        Purchase purchase = new Purchase(serialNumber,clientId, toyId, Calendar.getInstance().get(Calendar.YEAR));
        purchase.setId(id);
        try {
            purchaseRepository.save(purchase);
        }catch(ValidatorException | IllegalArgumentException e){
            throw new StoreServiceException(e.getMessage());
        }
    }

    /**
       Get the last n purchases.
       @return  purchases -  the list fo the last n purchases | null if n is bigger than the number of purchases
       @throws StoreServiceException
                 if n is bigger than the number of purchases
    */
    public List<Purchase> getLastNPurchases(int n) throws StoreServiceException {
        Iterable<Purchase> iterator = purchaseRepository.findAll();
        List<Purchase> purchases = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        int finalN = purchases.size();
        n = Optional.of(n).filter(p -> p < finalN).orElse(purchases.size());

        purchases.sort(new PurchaseYearComparator());
        return purchases.subList(purchases.size() - n, purchases.size());
    }

    /**
     * Get the number of purchases from a given year
     *
     * @return Int with the number of purchases
     * @throws StoreServiceException
     *          if the purchaseRepository is empty
     */

    public long getNumberOfPurchasesForYear(int year) throws StoreServiceException{

        Iterable<Purchase> iterator = purchaseRepository.findAll();
        List<Purchase> purchases = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!purchases.isEmpty()).filter(a -> a).orElseThrow(() ->
                new StoreServiceException("getNumberOfPurchasesForYear: empty repository of purchases.")
        );

        return purchases.stream()
                .filter(p -> p.getPurchaseYear() == year)
                .count();
    }

    /**
     * Get the most popular material purchased
     *
     * @return String with the name of the most popular material
     * @throws StoreServiceException
     *          if the purchaseRepository is empty
     */

    public String getMostPopularMaterial() throws StoreServiceException{

        Iterable<Purchase> iterator = purchaseRepository.findAll();
        List<Purchase> purchases = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!purchases.isEmpty()).filter(a -> a).orElseThrow(() ->
                new StoreServiceException("getMostPopularMaterial: empty repository of purchases.")
        );

        // take the purchased toys
        List<Toy> purchasedToys = purchases.stream()
                .map(t -> toyRepository.findOne(t.getToyId()).get())
                .collect(Collectors.toList());

        Map<String, Integer> material_and_frequency = new HashMap<String, Integer>();
        purchasedToys.forEach(t -> material_and_frequency.put(t.getMaterial(), material_and_frequency.get(t.getMaterial()) == null ? 1 : material_and_frequency.get(t.getMaterial())+1));

        return material_and_frequency.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList())
                .stream().limit(1L)
                .collect(Collectors.toList())
                .get(0)
                .getKey();
    }

    /**
     *  Get the toy with the biggest weight that was purchased.
     *  @param: no param
     *  @throws: StoreException if there are no purchases in the repository
     *  @return: Toy "With the heaviest weight"
     */
    public Toy getHeaviestPurchasedToy() throws StoreServiceException
    {

        Iterable<Purchase> iterator = purchaseRepository.findAll();
        List<Purchase> purchases = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!purchases.isEmpty()).filter(a -> a).orElseThrow(() ->
                new StoreServiceException("in getHeaviestPurchasedToy: empty repository for purchase.")
        );

        // take the purchased toys
        List<Toy> purchasedToys = purchases.stream()
                .map(t -> toyRepository.findOne(t.getToyId()).get())
                .collect(Collectors.toList());

        purchasedToys.sort(new ToyWeightComparator());
        return purchasedToys.get(0);
}

    /**
     * Get the most expensive purchase made
     *
     * @return Purchase: the most expensive purchase | null if the purchaseRepository is empty
     * @throws StoreServiceException
     *          if the purchaseRepository is empty
     */
    public Purchase getMostExpensivePurchase() throws StoreServiceException{

        Iterable<Purchase> iterator = purchaseRepository.findAll();
        List<Purchase> purchases = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!purchases.isEmpty()).filter(p -> p).orElseThrow(() ->
            new StoreServiceException("StoreServiceException in getMostExpensivePurchase: empty repository of purchases.")
        );

        // take the purchased toys
        List<Toy> purchasedToys = StreamSupport
                .stream(purchaseRepository.findAll().spliterator(), false)
                .map(p -> toyRepository.findOne(p.getToyId()).get())
                .collect(Collectors.toList());

        purchasedToys.sort(new ToyPriceComparator());
        // the most expensive toy
        Toy toy = purchasedToys.get(0);

        // go through purchases and find the one with the given toy id
        long a = toy.getId();
        String b = toy.getSerialNumber();
        long cnt = purchases.stream()
                .filter(p -> p.getToyId() == toy.getId())
                .count();

        Optional.of(cnt).filter(c -> c > 0).orElseThrow(() ->
                new StoreServiceException("Could not find anything.")
        );

        return purchases.stream()
                .filter(p -> p.getToyId() == toy.getId())
                .collect(Collectors.toList())
                .get(0);
    }

    /**
     * Get the average weight of purchased toys
     *
     * @return Long with the average weight of the toys
     * @throws StoreServiceException
     *          if the purchaseRepository is empty
     */

    public long getAveragePurchasedToysWeight() throws StoreServiceException{

        Iterable<Purchase> iterator = purchaseRepository.findAll();
        List<Purchase> purchases = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!purchases.isEmpty()).filter(a -> a).orElseThrow(() ->
                new StoreServiceException("StoreService Exception in getAveragePurchasedToysWeight: empty repository of purchases.")
        );

        // take the purchased toys
        List<Toy> purchasedToys = purchases.stream()
                .map(t -> toyRepository.findOne(t.getToyId()).get())
                .collect(Collectors.toList());

        long sm = purchasedToys.stream()
                .map(t -> t.getWeight())
                .reduce(0, (acc, iter) -> acc += iter);
        return sm / purchasedToys.size();
    }

    /**
     * Retrieve all purchases
     *
     * @return  Set<Purchase> : set of purchases
     */

    public Set<Purchase> getAllPurchases()
    {
        Iterable<Purchase> purchases = purchaseRepository.findAll();
        return StreamSupport.stream(purchases.spliterator(), false).collect(Collectors.toSet());
    }

    /**
     * Delete all purchases for a given client
     *
     * @param id : long id of the client whose adoptions should be deleted
     */

    public void deletePurchasesForClient(long id) throws StoreServiceException {

        Iterable<Purchase> iterator = purchaseRepository.findAll();
        List<Purchase> purchases = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        purchases.stream()
                .filter(client -> client.getClientId() == id)
                .forEach(client -> purchaseRepository.delete(client.getId()));
    }

    /**
     * Delete all purchases for a given toy
     *
     * @param id : long id of the toy whose adoptions should be deleted
     */

    public void deletePurchasesForToy(long id) throws StoreServiceException {
        Iterable<Purchase> iterator = purchaseRepository.findAll();
        List<Purchase> purchases = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        purchases.stream()
                .filter(pet -> pet.getToyId() == id)
                .forEach(pet -> purchaseRepository.delete(pet.getId()));
    }
}