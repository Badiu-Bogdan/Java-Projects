package repository.DB;

import domain.Adoption.Adoption;
import domain.validators.AdoptionValidator;
import domain.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.DB.exceptions.DBRepositoryAdoptionException;
import repository.Repository;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class DBRepositoryAdoptionTest {

    private static final Long ID = new Long(1);

    private Validator<Adoption> adoptionValidator;
    private Repository<Long, Adoption> adoptionRepository;

    @Before
    public void setUp() throws Exception {
        adoptionValidator = new AdoptionValidator();
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testtablename");
        ((DBRepositoryAdoption) adoptionRepository).dropTable();

    }

    @After
    public void tearDown() throws Exception {

        ((DBRepositoryAdoption) adoptionRepository).dropTable();
        adoptionValidator=null;
        adoptionRepository=null;

    }

    /**
     * Tests finding a adoption from the repository.
     * An adoption is created, saved in the repository and then retrieved.
     *
     * @throws DBRepositoryAdoptionException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws DBRepositoryAdoptionException {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");
        Adoption adoption = new Adoption("50001",1L,1L,2019);
        adoption.setId(ID);
        assertTrue(adoptionRepository.save(adoption).isPresent());
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        assertTrue(adoptionRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Adoption objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two adoptions)
     *
     * @throws DBRepositoryAdoptionException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws DBRepositoryAdoptionException {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        Adoption adoption2 = new Adoption("50002",2L,2L,2020);
        adoption1.setId(ID);
        adoption2.setId(ID+1);
        assertTrue(adoptionRepository.save(adoption1).isPresent());
        assertTrue(adoptionRepository.save(adoption2).isPresent());
        Iterable<Adoption> adoptions = adoptionRepository.findAll();
        Iterator<Adoption> iterator = adoptions.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(adoptionRepository.delete(ID).isPresent());
        assertTrue(adoptionRepository.delete(ID+1).isPresent());

    }

    /**
     * Tests whether a adoption can be saved in the repository.
     * (should result in no exceptions)
     *
     * @throws DBRepositoryAdoptionException
     * if the save operation did not succeed
     */
    @Test
    public void testSave() throws DBRepositoryAdoptionException {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        adoption1.setId(ID);
        assertTrue(adoptionRepository.save(adoption1).isPresent());
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        assertTrue(adoptionRepository.delete(ID).isPresent());

    }

    /**
     * Tests whether the same adoption can be saved in the repository twice.
     * (should catch an exception)
     *
     * @throws DBRepositoryAdoptionException
     * if the save operation did not succeed
     */
    @Test(expected = DBRepositoryAdoptionException.class)
    public void testSaveException() throws DBRepositoryAdoptionException {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        adoption1.setId(ID);
        assertTrue(adoptionRepository.save(adoption1).isPresent());
        Adoption adoption2 = new Adoption("50002",2L,2L,2020);
        adoption2.setId(ID);
        try {
            adoptionRepository.save(adoption2);
        }catch(DBRepositoryAdoptionException e){
            adoptionRepository.delete(ID);
            throw e;
        }


    }

    /**
     * Tests whether a adoption can be removed from the repository.
     * (should result in no exceptions)
     *
     * @throws DBRepositoryAdoptionException
     * if the deletion did not succeed
     */
    @Test
    public void testDelete() throws DBRepositoryAdoptionException {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        adoption1.setId(ID);
        assertTrue(adoptionRepository.save(adoption1).isPresent());
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        assertTrue(adoptionRepository.delete(ID).isPresent());
        assertFalse(adoptionRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether a adoption can be removed from the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryAdoptionException
     * if the delete did not succeed
     */
    @Test(expected = DBRepositoryAdoptionException.class)
    public void testDeleteException() throws DBRepositoryAdoptionException {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        adoption1.setId(ID);
        adoptionRepository.delete(ID);


    }

    /**
     * Tests whether a adoption can be updated in the repository.
     * A new adoption is created and updated in the repository.
     * Then the old adoption is retrieved from the repository (should not be found)
     * and the new adoption is retrieved from the repository as well (should be found)
     *
     * @throws DBRepositoryAdoptionException
     * if the update did not succeed
     */
    @Test
    public void testUpdate() throws DBRepositoryAdoptionException {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        adoption1.setId(ID);
        assertTrue(adoptionRepository.save(adoption1).isPresent());
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        Adoption adoption2 = new Adoption("50002",2L,2L,2020);
        adoption2.setId(ID);
        adoptionRepository.update(adoption2);
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        adoptionRepository.delete(ID);
    }


    /**
     * Tests whether a adoption can be updated in the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryAdoptionException
     * if the update did not succeed
     */
    @Test(expected = DBRepositoryAdoptionException.class)
    public void testUpdateException() throws DBRepositoryAdoptionException {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");
        Adoption adoption = new Adoption("50001",1L,1L,2019);
        adoption.setId(ID);
        adoptionRepository.update(adoption);


        ((DBRepositoryAdoption) adoptionRepository).dropTable();
    }

    /**
     * Tests whether we can get the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryAdoptionException
     * if getTableName did not succeed
     */
    @Test
    public void testGetTableName() throws DBRepositoryAdoptionException {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");
        assertSame("testTableName", ((DBRepositoryAdoption) adoptionRepository).getTableName());


    }

    /**
     * Tests whether we can set the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryAdoptionException
     * if setTableName did not succeed
     */
    @Test
    public void testSetTableName() throws DBRepositoryAdoptionException  {
        adoptionRepository = new DBRepositoryAdoption<>(adoptionValidator,"testTableName");

        ((DBRepositoryAdoption) adoptionRepository).setTableName("testTableName");
         assertSame("testTableName", ((DBRepositoryAdoption) adoptionRepository).getTableName());


    }


}