package repository;

import domain.Pet.Pet;
import domain.validators.PetValidator;
import domain.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import domain.validators.exceptions.ValidatorException;
import repository.XML.XMLRepositoryPet;

import java.util.Iterator;
import java.util.Optional;

import static org.junit.Assert.*;

/*
Author: Andrei A.
*/

public class InMemoryRepositoryTest {

    private static final Long ID = new Long(1);

    private Validator<Pet> petValidator;
    private Repository<Long, Pet> petRepository;

    @Before
    public void setUp() throws Exception {
        petValidator = new PetValidator();
        petRepository = new InMemoryRepository<>(petValidator);
    }

    @After
    public void tearDown() throws Exception {
        petValidator=null;
        petRepository=null;
    }

    /**
     * Tests finding a pet from the repository.
     * A pet is created, saved in the repository and then retrieved.
     *
     * @throws InMemoryRepositoryException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws InMemoryRepositoryException {
        petRepository = new InMemoryRepository<>(petValidator);
        Pet pet = new Pet("921","name1","breed1",2019);
        pet.setId(ID);
        petRepository.save(pet);
        assertTrue(petRepository.findOne(ID).isPresent());

        try{
            petRepository.findOne(null);
            fail();
        }catch(InMemoryRepositoryException e) {}
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Pet objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two pets)
     *
     * @throws InMemoryRepositoryException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws InMemoryRepositoryException {
        petRepository = new InMemoryRepository<>(petValidator);
        Pet pet1 = new Pet("921","name1","breed1",2019);
        Pet pet2 = new Pet("922","name2","breed2",2016);
        pet1.setId(ID);
        pet2.setId(ID+1);
        petRepository.save(pet1);
        petRepository.save(pet2);
        Iterable<Pet> pets = petRepository.findAll();
        Iterator<Pet> iterator = pets.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    /**
     * Tests whether a pet can be saved in the repository.
     * (should result in no exceptions)
     *
     */
    @Test
    public void testSave() {
        petRepository = new InMemoryRepository<>(petValidator);
        Pet pet1 = new Pet("70001","name1","breed1",2019);
        try{
            petRepository.save(null);
            fail();
        }catch(IllegalArgumentException e){ }

        pet1.setId(ID);

        try{
            pet1.setBirthDate(-200);
            petRepository.save(pet1);
            fail();
        }catch(ValidatorException e){
            pet1.setBirthDate(2019);
        }

        assertFalse(petRepository.save(pet1).isPresent());
        assertTrue(petRepository.findOne(ID).isPresent());

        Pet pet2 = new Pet("70002","name1","breed1",2019);
        pet2.setId(ID);
        assertTrue(petRepository.save(pet2).isPresent());

        petRepository.delete(pet1.getId());
    }

    /**
     * Tests whether a pet can be removed from the repository.
     * (should result in no exceptions)
     *
     */
    @Test
    public void testDelete() {
        petRepository = new InMemoryRepository<>(petValidator);
        Pet pet1 = new Pet("921","name1","breed1",2019);
        pet1.setId(ID);
        petRepository.save(pet1);
        assertTrue(petRepository.findOne(ID).isPresent());
        assertTrue(petRepository.delete(ID).isPresent());
        assertFalse(petRepository.findOne(ID).isPresent());
        assertFalse(petRepository.delete(ID).isPresent());

        try{
            petRepository.delete(null);
            fail();
        }catch(IllegalArgumentException e) {}
    }

    /**
     * Tests whether a pet can be updated in the repository.
     * A new pet is created and updated in the repository.
     * Then the old pet is retrieved from the repository (should not be found)
     * and the new pet is retrieved from the repository as well (should be found)
     *
     */
    @Test
    public void testUpdate() {
        petRepository = new InMemoryRepository<>(petValidator);
        Pet pet1 = new Pet("40001","name1","breed1",2019);
        pet1.setId(ID);
        assertFalse(petRepository.save(pet1).isPresent());
        assertTrue(petRepository.findOne(ID).isPresent());
        Pet pet2 = new Pet("40002","name2","breed2",2016);
        pet2.setId(ID);
        assertFalse(petRepository.update(pet2).isPresent()); // should succeed
        assertTrue(petRepository.findOne(ID).isPresent());
        petRepository.delete(ID);
    }

    /**
     * Tests whether a pet can be updated in the repository.
     * (should result in an exception)
     *
     * @throws ValidatorException
     * if the update did not succeed
     */
    @Test(expected = ValidatorException.class)
    public void testUpdateException() throws ValidatorException {
        petRepository = new InMemoryRepository<>(petValidator);

        try{
            petRepository.update(null);
            fail();
        }catch(IllegalArgumentException e) {}

        Pet pet1 = new Pet("921","name1","breed1",2019);
        pet1.setId(ID);
        pet1.setBirthDate(-100);
        petRepository.update(pet1);

    }
}