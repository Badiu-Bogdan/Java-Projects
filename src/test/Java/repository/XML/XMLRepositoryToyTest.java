package repository.XML;

import domain.Pet.Pet;
import domain.Toy.Toy;
import domain.Toy.Toy;
import domain.validators.ToyValidator;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryToyException;
import repository.XML.exceptions.XMLRepositoryToyException;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class XMLRepositoryToyTest {

    private static final Long ID = new Long(1);

    private Validator<Toy> toyValidator;
    private Repository<Long, Toy> toyRepository;

    @Before
    public void setUp() throws Exception {
        toyValidator = new ToyValidator();
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
    }

    @After
    public void tearDown() throws Exception {
        toyValidator=null;
        toyRepository=null;
    }

    /**
     * Tests finding a toy from the repository.
     * A toy is created, saved in the repository and then retrieved.
     *
     * @throws XMLRepositoryToyException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws XMLRepositoryToyException {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
        Toy toy = new Toy("50001","name1",100,"material1",1.99);
        toy.setId(ID);
        assertFalse(toyRepository.save(toy).isPresent());
        assertTrue(toyRepository.findOne(ID).isPresent());
        assertTrue(toyRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Toy objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two toys)
     *
     * @throws XMLRepositoryToyException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws XMLRepositoryToyException {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        Toy toy2 = new Toy("50002","name2",200,"material2",2.99);
        toy1.setId(ID);
        toy2.setId(ID+1);
        assertFalse(toyRepository.save(toy1).isPresent());
        assertFalse(toyRepository.save(toy2).isPresent());
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
     */
    @Test
    public void testSave() {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        try{
            toyRepository.save(null);
            fail();
        }catch(IllegalArgumentException e){ }

        toy1.setId(ID+100);

        try{
            toy1.setWeight(-200);
            toyRepository.save(toy1);
            fail();
        }catch(ValidatorException e){
            toy1.setWeight(2019);
        }

        assertFalse(toyRepository.save(toy1).isPresent());
        assertTrue(toyRepository.findOne(ID+100).isPresent());

        Toy toy2 = new Toy("50002","name2",200,"material2",2.99);
        toy2.setId(ID+100);
        assertTrue(toyRepository.save(toy2).isPresent());

        toyRepository.delete(toy1.getId());
    }

    /**
     * Tests whether a toy can be removed from the repository.
     * (should result in no exceptions)
     *
     */
    @Test
    public void testDelete() {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        toy1.setId(ID);
        assertFalse(toyRepository.save(toy1).isPresent());
        assertTrue(toyRepository.findOne(ID).isPresent());
        assertTrue(toyRepository.delete(ID).isPresent());
        assertFalse(toyRepository.findOne(ID).isPresent());
        assertFalse(toyRepository.delete(ID).isPresent());

        try{
            toyRepository.delete(null);
            fail();
        }catch(IllegalArgumentException e) {}
    }

    /**
     * Tests whether a toy can be removed from the repository.
     * (should result in an exception)
     *
     * @throws IllegalArgumentException
     * if the delete did not succeed
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteException() throws IllegalArgumentException {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        toyRepository.delete(null);
    }

    /**
     * Tests whether a toy can be updated in the repository.
     * A new toy is created and updated in the repository.
     * Then the old toy is retrieved from the repository (should not be found)
     * and the new toy is retrieved from the repository as well (should be found)
     *
     * @throws XMLRepositoryToyException
     * if the update did not succeed
     */
    @Test
    public void testUpdate() throws XMLRepositoryToyException {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        toy1.setId(ID);
        assertFalse(toyRepository.save(toy1).isPresent());
        assertTrue(toyRepository.findOne(ID).isPresent());
        Toy toy2 = new Toy("50002","name2",200,"material2",2.99);
        toy2.setId(ID);
        assertFalse(toyRepository.update(toy2).isPresent()); // should succeed
        assertTrue(toyRepository.findOne(ID).isPresent());
        toyRepository.delete(ID);
    }

    /**
     * Tests whether a toy can be updated in the repository.
     * (should result in an exception)
     *
     * @throws ValidatorException
     * if the update did not succeed
     */
    @Test(expected = ValidatorException.class)
    public void testUpdateException() throws ValidatorException {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
        try{
            toyRepository.update(null);
            fail();
        }catch(IllegalArgumentException e) {}

        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        toy1.setId(ID);
        toy1.setWeight(-100);
        toyRepository.update(toy1);
    }

    /**
     * Tests whether the file name was retrieved correctly
     */
    @Test
    public void getFileName() {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "testFileName");
        assertSame("testFileName", ((XMLRepositoryToy) toyRepository).getFileName());
    }

    /**
     * Tests whether the file name was set correctly
     */
    @Test
    public void setFileName() {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "testFileName");
        ((XMLRepositoryToy) toyRepository).setFileName("testFileName1");
        assertSame("testFileName1", ((XMLRepositoryToy) toyRepository).getFileName());
    }

    /**
     * Tests whether a toy can be saved to xml format
     *
     */
    @Test
    public void saveToXML() {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        toy1.setId(ID);
        ((XMLRepositoryToy)toyRepository).saveToXML(toy1);
        assertTrue(toyRepository.delete(toy1.getId()).isPresent());
    }

    /**
     * Tests whether the repository can remove all toy instances from the xml file
     *
     */
    @Test
    public void deleteAll() {
        toyRepository = new XMLRepositoryToy<>(toyValidator, "test/toysTest");
        ((XMLRepositoryToy)toyRepository).deleteAll();
    }

    /**
     * Tests whether more toys can be saved to xml format (from a list)
     *
     */
    @Test
    public void saveEntitiesToXML() {
        List<Toy> toys = new ArrayList<>();
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        Toy toy2 = new Toy("50002","name2",200,"material2",2.99);
        Toy toy3 = new Toy("50003","name3",300,"material3",3.99);
        toy1.setId(ID);
        toy2.setId(ID+1);
        toy3.setId(ID+2);
        toys.add(toy1);
        toys.add(toy2);
        toys.add(toy3);
        ((XMLRepositoryToy)toyRepository).saveEntitiesToXML(toys);
        ((XMLRepositoryToy)toyRepository).deleteAll();
    }

    /**
     * Tests whether more toys can be saved to xml format (from a map)
     *
     */
    @Test
    public void saveMapToXML() {
        Map<Long, Toy> toys = new HashMap<>();
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        Toy toy2 = new Toy("50002","name2",200,"material2",2.99);
        Toy toy3 = new Toy("50003","name3",300,"material3",3.99);
        toy1.setId(ID);
        toy2.setId(ID+1);
        toy3.setId(ID+2);
        toys.put(toy1.getId(), toy1);
        toys.put(toy2.getId(), toy2);
        toys.put(toy3.getId(), toy3);
        ((XMLRepositoryToy)toyRepository).saveMapToXML(toys);
        ((XMLRepositoryToy)toyRepository).deleteAll();
    }
}