package repository.DB;


import domain.Pet.Pet;
import domain.validators.PetValidator;
import domain.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.DB.exceptions.DBRepositoryPetException;
import repository.Repository;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class DBRepositoryPetTest {

    private static final Long ID = new Long(1);

    private Validator<Pet> petValidator;
    private Repository<Long, Pet> petRepository;

    @Before
    public void setUp() throws Exception {
        petValidator = new PetValidator();
        petRepository = new DBRepositoryPet<>(petValidator,"testtablename");
        ((DBRepositoryPet) petRepository).dropTable();

    }

    @After
    public void tearDown() throws Exception {

        ((DBRepositoryPet) petRepository).dropTable();
        petValidator=null;
        petRepository=null;

    }

    /**
     * Tests finding a pet from the repository.
     * An pet is created, saved in the repository and then retrieved.
     *
     * @throws DBRepositoryPetException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");
        Pet pet = new Pet("50001","name1","breed1",2019);
        pet.setId(ID);
        assertTrue(petRepository.save(pet).isPresent());
        assertTrue(petRepository.findOne(ID).isPresent());
        assertTrue(petRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Pet objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two pets)
     *
     * @throws DBRepositoryPetException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");
        Pet pet1 = new Pet("50001","name1","breed1",2019);
        Pet pet2 = new Pet("50002","name2","breed2",2020);
        pet1.setId(ID);
        pet2.setId(ID+1);
        assertTrue(petRepository.save(pet1).isPresent());
        assertTrue(petRepository.save(pet2).isPresent());
        Iterable<Pet> pets = petRepository.findAll();
        Iterator<Pet> iterator = pets.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(petRepository.delete(ID).isPresent());
        assertTrue(petRepository.delete(ID+1).isPresent());

    }

    /**
     * Tests whether a pet can be saved in the repository.
     * (should result in no exceptions)
     *
     * @throws DBRepositoryPetException
     * if the save operation did not succeed
     */
    @Test
    public void testSave() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");
        Pet pet1 = new Pet("50001","name1","breed1",2019);
        pet1.setId(ID);
        assertTrue(petRepository.save(pet1).isPresent());
        assertTrue(petRepository.findOne(ID).isPresent());
        assertTrue(petRepository.delete(ID).isPresent());

    }

    /**
     * Tests whether the same pet can be saved in the repository twice.
     * (should catch an exception)
     *
     * @throws DBRepositoryPetException
     * if the save operation did not succeed
     */
    @Test(expected = DBRepositoryPetException.class)
    public void testSaveException() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");
        Pet pet1 = new Pet("50001","name1","breed1",2019);
        pet1.setId(ID);
        assertTrue(petRepository.save(pet1).isPresent());
        Pet pet2 = new Pet("50002","name2","breed2",2020);
        pet2.setId(ID);
        try {
            petRepository.save(pet2);
        }catch(DBRepositoryPetException e){
            petRepository.delete(ID);
            throw e;
        }


    }

    /**
     * Tests whether a pet can be removed from the repository.
     * (should result in no exceptions)
     *
     * @throws DBRepositoryPetException
     * if the deletion did not succeed
     */
    @Test
    public void testDelete() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");
        Pet pet1 = new Pet("50001","name1","breed1",2019);
        pet1.setId(ID);
        assertTrue(petRepository.save(pet1).isPresent());
        assertTrue(petRepository.findOne(ID).isPresent());
        assertTrue(petRepository.delete(ID).isPresent());
        assertFalse(petRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether a pet can be removed from the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryPetException
     * if the delete did not succeed
     */
    @Test(expected = DBRepositoryPetException.class)
    public void testDeleteException() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");
        Pet pet1 = new Pet("50001","name1","breed1",2019);
        pet1.setId(ID);
        petRepository.delete(ID);


    }

    /**
     * Tests whether a pet can be updated in the repository.
     * A new pet is created and updated in the repository.
     * Then the old pet is retrieved from the repository (should not be found)
     * and the new pet is retrieved from the repository as well (should be found)
     *
     * @throws DBRepositoryPetException
     * if the update did not succeed
     */
    @Test
    public void testUpdate() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");
        Pet pet1 = new Pet("50001","name1","breed1",2019);
        pet1.setId(ID);
        assertTrue(petRepository.save(pet1).isPresent());
        assertTrue(petRepository.findOne(ID).isPresent());
        Pet pet2 = new Pet("50002","name2","breed2",2020);
        pet2.setId(ID);
        petRepository.update(pet2);
        assertTrue(petRepository.findOne(ID).isPresent());
        petRepository.delete(ID);
    }


    /**
     * Tests whether a pet can be updated in the repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryPetException
     * if the update did not succeed
     */
    @Test(expected = DBRepositoryPetException.class)
    public void testUpdateException() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");
        Pet pet = new Pet("50001","name1","breed1",2019);
        pet.setId(ID);
        petRepository.update(pet);


        ((DBRepositoryPet) petRepository).dropTable();
    }

    /**
     * Tests whether we can get the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryPetException
     * if getTableName did not succeed
     */
    @Test
    public void testGetTableName() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");
        assertSame("testTableName", ((DBRepositoryPet) petRepository).getTableName());


    }

    /**
     * Tests whether we can set the tableName of the database repository.
     * (should result in an exception)
     *
     * @throws DBRepositoryPetException
     * if setTableName did not succeed
     */
    @Test
    public void testSetTableName() throws DBRepositoryPetException {
        petRepository = new DBRepositoryPet<>(petValidator,"testTableName");

        ((DBRepositoryPet) petRepository).setTableName("testTableName");
        assertSame("testTableName", ((DBRepositoryPet) petRepository).getTableName());


    }


}