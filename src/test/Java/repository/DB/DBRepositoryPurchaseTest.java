package repository.DB;

import domain.Purchase.Purchase;
import domain.validators.PurchaseValidator;
import domain.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.DB.exceptions.DBRepositoryPurchaseException;
import repository.Repository;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class DBRepositoryPurchaseTest {

    private static final Long ID = new Long(1);

    private Validator<Purchase> purchaseValidator;
    private Repository<Long, Purchase> purchaseRepository;

    @Before
    public void setUp() throws Exception {
        purchaseValidator = new PurchaseValidator();
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testtablename");
        ((DBRepositoryPurchase) purchaseRepository).dropTable();

    }

    @After
    public void tearDown() throws Exception {

        ((DBRepositoryPurchase) purchaseRepository).dropTable();
        purchaseValidator=null;
        purchaseRepository=null;

    }

    /**
     * Tests finding a purchase from the repository.
     * An purchase is created, saved in the repository and then retrieved.
     *
     * @throws DBRepositoryPurchaseException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws DBRepositoryPurchaseException {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");
        Purchase purchase = new Purchase("50001",1L,1L,2019);
        purchase.setId(ID);
        assertTrue(purchaseRepository.save(purchase).isPresent());
        assertTrue(purchaseRepository.findOne(ID).isPresent());
        assertTrue(purchaseRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Purchase objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two purchases)
     *
     * @throws DBRepositoryPurchaseException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws DBRepositoryPurchaseException {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        Purchase purchase2 = new Purchase("50002",2L,2L,2020);
        purchase1.setId(ID);
        purchase2.setId(ID+1);
        assertTrue(purchaseRepository.save(purchase1).isPresent());
        assertTrue(purchaseRepository.save(purchase2).isPresent());
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
     * @throws DBRepositoryPurchaseException
     * if the save operation did not succeed
     */
    @Test
    public void testSave() throws DBRepositoryPurchaseException {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        purchase1.setId(ID);
        assertTrue(purchaseRepository.save(purchase1).isPresent());
        assertTrue(purchaseRepository.findOne(ID).isPresent());
        assertTrue(purchaseRepository.delete(ID).isPresent());

    }

    /**
     * Tests whether the same purchase can be saved in the repository twice.
     * (should catch an exception)
     *
     * @throws DBRepositoryPurchaseException
     * if the save operation did not succeed
     */
    @Test(expected = DBRepositoryPurchaseException.class)
    public void testSaveException() throws DBRepositoryPurchaseException {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        purchase1.setId(ID);
        assertTrue(purchaseRepository.save(purchase1).isPresent());
        Purchase purchase2 = new Purchase("50002",2L,2L,2020);
        purchase2.setId(ID);
        try {
            purchaseRepository.save(purchase2);
        }catch(DBRepositoryPurchaseException e){
            purchaseRepository.delete(ID);
            throw e;
        }


    }

    /**
     * Tests whether a purchase can be removed from the repository.
     * (should result in no exceptions)
     *
     * @throws DBRepositoryPurchaseException
     * if the deletion did not succeed
     */
    @Test
    public void testDelete() throws DBRepositoryPurchaseException {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        purchase1.setId(ID);
        assertTrue(purchaseRepository.save(purchase1).isPresent());
        assertTrue(purchaseRepository.findOne(ID).isPresent());
        assertTrue(purchaseRepository.delete(ID).isPresent());
        assertFalse(purchaseRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether a purchase can be removed from the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryPurchaseException
     * if the delete did not succeed
     */
    @Test(expected = DBRepositoryPurchaseException.class)
    public void testDeleteException() throws DBRepositoryPurchaseException {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        purchase1.setId(ID);
        purchaseRepository.delete(ID);


    }

    /**
     * Tests whether a purchase can be updated in the repository.
     * A new purchase is created and updated in the repository.
     * Then the old purchase is retrieved from the repository (should not be found)
     * and the new purchase is retrieved from the repository as well (should be found)
     *
     * @throws DBRepositoryPurchaseException
     * if the update did not succeed
     */
    @Test
    public void testUpdate() throws DBRepositoryPurchaseException {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");
        Purchase purchase1 = new Purchase("50001",1L,1L,2019);
        purchase1.setId(ID);
        assertTrue(purchaseRepository.save(purchase1).isPresent());
        assertTrue(purchaseRepository.findOne(ID).isPresent());
        Purchase purchase2 = new Purchase("50002",2L,2L,2020);
        purchase2.setId(ID);
        purchaseRepository.update(purchase2);
        assertTrue(purchaseRepository.findOne(ID).isPresent());
        purchaseRepository.delete(ID);
    }


    /**
     * Tests whether a purchase can be updated in the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryPurchaseException
     * if the update did not succeed
     */
    @Test(expected = DBRepositoryPurchaseException.class)
    public void testUpdateException() throws DBRepositoryPurchaseException {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");
        Purchase purchase = new Purchase("50001",1L,1L,2019);
        purchase.setId(ID);
        purchaseRepository.update(purchase);


        ((DBRepositoryPurchase) purchaseRepository).dropTable();
    }

    /**
     * Tests whether we can get the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryPurchaseException
     * if getTableName did not succeed
     */
    @Test
    public void testGetTableName() throws DBRepositoryPurchaseException {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");
        assertSame("testTableName", ((DBRepositoryPurchase) purchaseRepository).getTableName());


    }

    /**
     * Tests whether we can set the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryPurchaseException
     * if setTableName did not succeed
     */
    @Test
    public void testSetTableName() throws DBRepositoryPurchaseException  {
        purchaseRepository = new DBRepositoryPurchase<>(purchaseValidator,"testTableName");

        ((DBRepositoryPurchase) purchaseRepository).setTableName("testTableName");
        assertSame("testTableName", ((DBRepositoryPurchase) purchaseRepository).getTableName());


    }


}