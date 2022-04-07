package repository.XML;

import domain.BaseEntity;
import domain.Pet.Pet;
import domain.validators.PetValidator;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryPetException;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class XMLRepositoryPetTest {

    private static final Long ID = new Long(1);

    private Validator<Pet> petValidator;
    private Repository<Long, Pet> petRepository;

    @Before
    public void setUp() throws Exception {
        petValidator = new PetValidator();
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
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
     * @throws XMLRepositoryPetException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws XMLRepositoryPetException {
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
        Pet pet = new Pet("90001","name1","breed1",2019);
        pet.setId(ID);
        assertFalse(petRepository.save(pet).isPresent());
        assertTrue(petRepository.findOne(ID).isPresent());
        assertTrue(petRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Pet objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two pets)
     *
     * @throws XMLRepositoryPetException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws XMLRepositoryPetException {
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
        Pet pet1 = new Pet("50001","name1","breed1",2019);
        Pet pet2 = new Pet("50002","name2","breed2",2016);
        pet1.setId(ID);
        pet2.setId(ID+1);
        assertFalse(petRepository.save(pet1).isPresent());
        assertFalse(petRepository.save(pet2).isPresent());
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
     */
    @Test
    public void testSave() {
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
        Pet pet1 = new Pet("70001","name1","breed1",2019);
        try{
            petRepository.save(null);
            fail();
        }catch(IllegalArgumentException e){ }

        pet1.setId(ID+100);

        try{
            pet1.setBirthDate(-200);
            petRepository.save(pet1);
            fail();
        }catch(ValidatorException e){
            pet1.setBirthDate(2019);
        }

        assertFalse(petRepository.save(pet1).isPresent());
        assertTrue(petRepository.findOne(ID+100).isPresent());

        Pet pet2 = new Pet("70002","name1","breed1",2019);
        pet2.setId(ID+100);
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
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
        Pet pet1 = new Pet("921","name1","breed1",2019);
        pet1.setId(ID);
        assertFalse(petRepository.save(pet1).isPresent());
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
     * Tests whether a pet can be removed from the repository.
     * (should result in an exception)
     *
     * @throws IllegalArgumentException
     * if the delete did not succeed
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteException() throws IllegalArgumentException {
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
        Pet pet1 = new Pet("80001","name1","breed1",2019);
        petRepository.delete(null);
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
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
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
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
        try{
            petRepository.update(null);
            fail();
        }catch(IllegalArgumentException e) {}

        Pet pet1 = new Pet("921","name1","breed1",2019);
        pet1.setId(ID);
        pet1.setBirthDate(-100);
        petRepository.update(pet1);
    }

    /**
     * Tests whether the file name was retrieved correctly
     */
    @Test
    public void getFileName() {
        petRepository = new XMLRepositoryPet<>(petValidator, "testFileName");
        assertSame("testFileName", ((XMLRepositoryPet) petRepository).getFileName());
    }

    /**
     * Tests whether the file name was set correctly
     */
    @Test
    public void setFileName() {
        petRepository = new XMLRepositoryPet<>(petValidator, "testFileName");
        ((XMLRepositoryPet) petRepository).setFileName("testFileName1");
        assertSame("testFileName1", ((XMLRepositoryPet) petRepository).getFileName());
    }

    /**
     * Tests whether a pet can be saved to xml format
     *
     */
    @Test
    public void saveToXML() {
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
        Pet pet1 = new Pet("60001","name1","breed1",2019);
        pet1.setId(ID);
        ((XMLRepositoryPet)petRepository).saveToXML(pet1);
        assertTrue(petRepository.delete(pet1.getId()).isPresent());
    }

    /**
     * Tests whether the repository can remove all pet instances from the xml file
     *
     */
    @Test
    public void deleteAll() {
        petRepository = new XMLRepositoryPet<>(petValidator, "test/petsTest");
        ((XMLRepositoryPet)petRepository).deleteAll();
    }

    /**
     * Tests whether more pets can be saved to xml format (from a list)
     *
     */
    @Test
    public void saveEntitiesToXML() {
        List<Pet> pets = new ArrayList<>();
        Pet pet1 = new Pet("60001","name1","breed1",2019);
        Pet pet2 = new Pet("60002","name2","breed2",2019);
        Pet pet3 = new Pet("60003","name3","breed3",2019);
        pet1.setId(ID);
        pet2.setId(ID+1);
        pet3.setId(ID+2);
        pets.add(pet1);
        pets.add(pet2);
        pets.add(pet3);
        ((XMLRepositoryPet)petRepository).saveEntitiesToXML(pets);
        ((XMLRepositoryPet)petRepository).deleteAll();
    }

    /**
     * Tests whether more pets can be saved to xml format (from a map)
     *
     */
    @Test
    public void saveMapToXML() {
        Map<Long, Pet> pets = new HashMap<>();
        Pet pet1 = new Pet("60001","name1","breed1",2019);
        Pet pet2 = new Pet("60002","name2","breed2",2019);
        Pet pet3 = new Pet("60003","name3","breed3",2019);
        pet1.setId(ID);
        pet2.setId(ID+1);
        pet3.setId(ID+2);
        pets.put(pet1.getId(), pet1);
        pets.put(pet2.getId(), pet2);
        pets.put(pet3.getId(), pet3);
        ((XMLRepositoryPet)petRepository).saveMapToXML(pets);
        ((XMLRepositoryPet)petRepository).deleteAll();
    }
}