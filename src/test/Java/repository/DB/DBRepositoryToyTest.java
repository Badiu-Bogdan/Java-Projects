package repository.DB;

import domain.Toy.Toy;
import domain.validators.ToyValidator;
import domain.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.DB.exceptions.DBRepositoryToyException;
import repository.Repository;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class DBRepositoryToyTest {

    private static final Long ID = new Long(1);

    private Validator<Toy> toyValidator;
    private Repository<Long, Toy> toyRepository;

    @Before
    public void setUp() throws Exception {
        toyValidator = new ToyValidator();
        toyRepository = new DBRepositoryToy<>(toyValidator,"testtablename");
        ((DBRepositoryToy) toyRepository).dropTable();

    }

    @After
    public void tearDown() throws Exception {

        ((DBRepositoryToy) toyRepository).dropTable();
        toyValidator=null;
        toyRepository=null;

    }

    /**
     * Tests finding a toy from the repository.
     * An toy is created, saved in the repository and then retrieved.
     *
     * @throws DBRepositoryToyException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws DBRepositoryToyException {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");
        Toy toy = new Toy("50001","name",2,"material1",26);
        toy.setId(ID);
        assertTrue(toyRepository.save(toy).isPresent());
        assertTrue(toyRepository.findOne(ID).isPresent());
        assertTrue(toyRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Toy objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two toys)
     *
     * @throws DBRepositoryToyException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws DBRepositoryToyException {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");
        Toy toy1 = new Toy("50001","name",2,"material1",26);
        Toy toy2 = new Toy("50002","name2",8,"material2",29);
        toy1.setId(ID);
        toy2.setId(ID+1);
        assertTrue(toyRepository.save(toy1).isPresent());
        assertTrue(toyRepository.save(toy2).isPresent());
        Iterable<Toy> toys = toyRepository.findAll();
        Iterator<Toy> iterator = toys.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(toyRepository.delete(ID).isPresent());
        assertTrue(toyRepository.delete(ID+1).isPresent());

    }

    /**
     * Tests whether a toy can be saved in the repository.
     * (should result in no exceptions)
     *
     * @throws DBRepositoryToyException
     * if the save operation did not succeed
     */
    @Test
    public void testSave() throws DBRepositoryToyException {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");
        Toy toy1 = new Toy("50001","name",2,"material1",26);
        toy1.setId(ID);
        assertTrue(toyRepository.save(toy1).isPresent());
        assertTrue(toyRepository.findOne(ID).isPresent());
        assertTrue(toyRepository.delete(ID).isPresent());

    }

    /**
     * Tests whether the same toy can be saved in the repository twice.
     * (should catch an exception)
     *
     * @throws DBRepositoryToyException
     * if the save operation did not succeed
     */
    @Test(expected = DBRepositoryToyException.class)
    public void testSaveException() throws DBRepositoryToyException {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");
        Toy toy1 = new Toy("50001","name",2,"material1",26);
        toy1.setId(ID);
        assertTrue(toyRepository.save(toy1).isPresent());
        Toy toy2 = new Toy("50002","name7",7,"material7",27);
        toy2.setId(ID);
        try {
            toyRepository.save(toy2);
        }catch(DBRepositoryToyException e){
            toyRepository.delete(ID);
            throw e;
        }


    }

    /**
     * Tests whether a toy can be removed from the repository.
     * (should result in no exceptions)
     *
     * @throws DBRepositoryToyException
     * if the deletion did not succeed
     */
    @Test
    public void testDelete() throws DBRepositoryToyException {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");
        Toy toy1 = new Toy("50001","name5",2,"material1",262019);
        toy1.setId(ID);
        assertTrue(toyRepository.save(toy1).isPresent());
        assertTrue(toyRepository.findOne(ID).isPresent());
        assertTrue(toyRepository.delete(ID).isPresent());
        assertFalse(toyRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether a toy can be removed from the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryToyException
     * if the delete did not succeed
     */
    @Test(expected = DBRepositoryToyException.class)
    public void testDeleteException() throws DBRepositoryToyException {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");
        Toy toy1 = new Toy("50001","name",2,"material1",26);
        toy1.setId(ID);
        toyRepository.delete(ID);


    }

    /**
     * Tests whether a toy can be updated in the repository.
     * A new toy is created and updated in the repository.
     * Then the old toy is retrieved from the repository (should not be found)
     * and the new toy is retrieved from the repository as well (should be found)
     *
     * @throws DBRepositoryToyException
     * if the update did not succeed
     */
    @Test
    public void testUpdate() throws DBRepositoryToyException {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");
        Toy toy1 = new Toy("50001","name",2,"material1",26);
        toy1.setId(ID);
        assertTrue(toyRepository.save(toy1).isPresent());
        assertTrue(toyRepository.findOne(ID).isPresent());
        Toy toy2 = new Toy("50002","name0",67,"material8",33);
        toy2.setId(ID);
        toyRepository.update(toy2);
        assertTrue(toyRepository.findOne(ID).isPresent());
        toyRepository.delete(ID);
    }


    /**
     * Tests whether a toy can be updated in the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryToyException
     * if the update did not succeed
     */
    @Test(expected = DBRepositoryToyException.class)
    public void testUpdateException() throws DBRepositoryToyException {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");
        Toy toy = new Toy("50001","name",2,"material1",262019);
        toy.setId(ID);
        toyRepository.update(toy);


        ((DBRepositoryToy) toyRepository).dropTable();
    }

    /**
     * Tests whether we can get the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryToyException
     * if getTableName did not succeed
     */
    @Test
    public void testGetTableName() throws DBRepositoryToyException {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");
        assertSame("testTableName", ((DBRepositoryToy) toyRepository).getTableName());


    }

    /**
     * Tests whether we can set the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryToyException
     * if setTableName did not succeed
     */
    @Test
    public void testSetTableName() throws DBRepositoryToyException  {
        toyRepository = new DBRepositoryToy<>(toyValidator,"testTableName");

        ((DBRepositoryToy) toyRepository).setTableName("testTableName");
        assertSame("testTableName", ((DBRepositoryToy) toyRepository).getTableName());


    }


}