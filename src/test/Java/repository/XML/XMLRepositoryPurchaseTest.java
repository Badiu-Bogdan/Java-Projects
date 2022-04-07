package repository.XML;

import domain.Pet.Pet;
import domain.Purchase.Purchase;
import domain.Purchase.Purchase;
import domain.validators.PurchaseValidator;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryStoreException;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class XMLRepositoryPurchaseTest {

    private static final Long ID = new Long(1);

    private Validator<Purchase> purchaseValidator;
    private Repository<Long, Purchase> purchaseRepository;

    @Before
    public void setUp() throws Exception {
        purchaseValidator = new PurchaseValidator();
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
    }

    @After
    public void tearDown() throws Exception {
        purchaseValidator=null;
        purchaseRepository=null;
    }

    /**
     * Tests finding a purchase from the repository.
     * A purchase is created, saved in the repository and then retrieved.
     *
     * @throws XMLRepositoryStoreException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws XMLRepositoryStoreException {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
        Purchase purchase = new Purchase("50001",1L,1L,2019);
        purchase.setId(ID);
        assertFalse(purchaseRepository.save(purchase).isPresent());
        assertTrue(purchaseRepository.findOne(ID).isPresent());
        assertTrue(purchaseRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Purchase objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two purchases)
     *
     * @throws XMLRepositoryStoreException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws XMLRepositoryStoreException {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        Purchase purchase2 = new Purchase("50002",2L,2L,2020);
        purchase1.setId(ID);
        purchase2.setId(ID+1);
        assertFalse(purchaseRepository.save(purchase1).isPresent());
        assertFalse(purchaseRepository.save(purchase2).isPresent());
        Iterable<Purchase> purchases = purchaseRepository.findAll();
        Iterator<Purchase> iterator = purchases.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(purchaseRepository.delete(ID).isPresent());
        assertTrue(purchaseRepository.delete(ID+1).isPresent());
    }

    /**
     * Tests whether a purchase can be saved in the repository.
     * (should result in no exceptions)
     *
     * @throws XMLRepositoryStoreException
     * if the save operation did not succeed
     */
    @Test
    public void testSave() throws XMLRepositoryStoreException {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        try{
            purchaseRepository.save(null);
            fail();
        }catch(IllegalArgumentException e){ }

        purchase1.setId(ID+100);

        try{
            purchase1.setPurchaseYear(-200);
            purchaseRepository.save(purchase1);
            fail();
        }catch(ValidatorException e){
            purchase1.setPurchaseYear(2019);
        }

        assertFalse(purchaseRepository.save(purchase1).isPresent());
        assertTrue(purchaseRepository.findOne(ID+100).isPresent());

        Purchase purchase2 = new Purchase("50002",2L,2L,2020);
        purchase2.setId(ID+100);
        assertTrue(purchaseRepository.save(purchase2).isPresent());

        purchaseRepository.delete(purchase1.getId());
    }

    /**
     * Tests whether a purchase can be removed from the repository.
     * (should result in no exceptions)
     *
     */
    @Test
    public void testDelete() {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        purchase1.setId(ID);
        assertFalse(purchaseRepository.save(purchase1).isPresent());
        assertTrue(purchaseRepository.findOne(ID).isPresent());
        assertTrue(purchaseRepository.delete(ID).isPresent());
        assertFalse(purchaseRepository.findOne(ID).isPresent());
        assertFalse(purchaseRepository.delete(ID).isPresent());

        try{
            purchaseRepository.delete(null);
            fail();
        }catch(IllegalArgumentException e) {}
    }

    /**
     * Tests whether a purchase can be removed from the repository.
     * (should result in an exception)
     *
     * @throws IllegalArgumentException
     * if the delete did not succeed
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteException() throws IllegalArgumentException {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        purchaseRepository.delete(null);
    }

    /**
     * Tests whether a purchase can be updated in the repository.
     * A new purchase is created and updated in the repository.
     * Then the old purchase is retrieved from the repository (should not be found)
     * and the new purchase is retrieved from the repository as well (should be found)
     *
     */
    @Test
    public void testUpdate() {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        purchase1.setId(ID);
        assertFalse(purchaseRepository.save(purchase1).isPresent());
        assertTrue(purchaseRepository.findOne(ID).isPresent());
        Purchase purchase2 = new Purchase("50002",2L,2L,2020);
        purchase2.setId(ID);
        assertFalse(purchaseRepository.update(purchase2).isPresent()); // should succeed
        assertTrue(purchaseRepository.findOne(ID).isPresent());
        purchaseRepository.delete(ID);
    }

    /**
     * Tests whether a purchase can be updated in the repository.
     * (should result in an exception)
     *
     * @throws ValidatorException
     * if the update did not succeed
     */
    @Test(expected = ValidatorException.class)
    public void testUpdateException() throws ValidatorException {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
        try{
            purchaseRepository.update(null);
            fail();
        }catch(IllegalArgumentException e) {}

        Purchase purchase = new Purchase("50001",1L,1L,2019);
        purchase.setId(ID);
        purchase.setPurchaseYear(-100);
        purchaseRepository.update(purchase);
    }

    /**
     * Tests whether the file name was retrieved correctly
     */
    @Test
    public void getFileName() {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "testFileName");
        assertSame("testFileName", ((XMLRepositoryStore) purchaseRepository).getFileName());
    }

    /**
     * Tests whether the file name was set correctly
     */
    @Test
    public void setFileName() {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "testFileName");
        ((XMLRepositoryStore) purchaseRepository).setFileName("testFileName1");
        assertSame("testFileName1", ((XMLRepositoryStore) purchaseRepository).getFileName());
    }

    /**
     * Tests whether a purchase can be saved to xml format
     *
     */
    @Test
    public void saveToXML() {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
        Purchase purchase1 = new Purchase("50001",1L,1L,2018);
        purchase1.setId(ID);
        ((XMLRepositoryStore)purchaseRepository).saveToXML(purchase1);
        assertTrue(purchaseRepository.delete(purchase1.getId()).isPresent());
    }

    /**
     * Tests whether the repository can remove all purchase instances from the xml file
     *
     */
    @Test
    public void deleteAll() {
        purchaseRepository = new XMLRepositoryStore<>(purchaseValidator, "test/purchasesTest");
        ((XMLRepositoryStore)purchaseRepository).deleteAll();
    }

    /**
     * Tests whether more purchases can be saved to xml format (from a list)
     *
     */
    @Test
    public void saveEntitiesToXML() {
        List<Purchase> purchases = new ArrayList<>();
        Purchase purchase1 = new Purchase("50001",1L,1L,2018);
        Purchase purchase2 = new Purchase("50002",2L,2L,2019);
        Purchase purchase3 = new Purchase("50003",3L,3L,2020);
        purchase1.setId(ID);
        purchase2.setId(ID+1);
        purchase3.setId(ID+2);
        purchases.add(purchase1);
        purchases.add(purchase2);
        purchases.add(purchase3);
        ((XMLRepositoryStore)purchaseRepository).saveEntitiesToXML(purchases);
        ((XMLRepositoryStore)purchaseRepository).deleteAll();
    }

    /**
     * Tests whether more purchases can be saved to xml format (from a map)
     *
     */
    @Test
    public void saveMapToXML() {
        Map<Long, Purchase> purchases = new HashMap<>();
        Purchase purchase1 = new Purchase("50001",1L,1L,2018);
        Purchase purchase2 = new Purchase("50002",2L,2L,2019);
        Purchase purchase3 = new Purchase("50003",3L,3L,2020);
        purchase1.setId(ID);
        purchase2.setId(ID+1);
        purchase3.setId(ID+2);
        purchases.put(purchase1.getId(), purchase1);
        purchases.put(purchase2.getId(), purchase2);
        purchases.put(purchase3.getId(), purchase3);
        ((XMLRepositoryStore)purchaseRepository).saveMapToXML(purchases);
        ((XMLRepositoryStore)purchaseRepository).deleteAll();
    }
}