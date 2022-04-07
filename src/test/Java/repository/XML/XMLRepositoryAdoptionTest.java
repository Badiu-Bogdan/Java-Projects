package repository.XML;

import domain.BaseEntity;
import domain.Adoption.Adoption;
import domain.Pet.Pet;
import domain.validators.AdoptionValidator;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryAdoptionException;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class XMLRepositoryAdoptionTest {

    private static final Long ID = new Long(1);

    private Validator<Adoption> adoptionValidator;
    private Repository<Long, Adoption> adoptionRepository;

    @Before
    public void setUp() throws Exception {
        adoptionValidator = new AdoptionValidator();
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
    }

    @After
    public void tearDown() throws Exception {
        adoptionValidator=null;
        adoptionRepository=null;
    }

    /**
     * Tests finding a adoption from the repository.
     * A adoption is created, saved in the repository and then retrieved.
     *
     * @throws XMLRepositoryAdoptionException
     * if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws XMLRepositoryAdoptionException {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
        Adoption adoption = new Adoption("50001",1L,1L,2019);
        adoption.setId(ID);
        assertFalse(adoptionRepository.save(adoption).isPresent());
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        assertTrue(adoptionRepository.delete(ID).isPresent());
    }

    /**
     * Tests whether the method findAll of the repository is working
     * Two Adoption objects are created, saved in the repository and then the
     * method findAll is invoked on the repository. The result is an Iterable
     * object that must contain two objects (the two adoptions)
     *
     * @throws XMLRepositoryAdoptionException
     * if the findAll operation did not succeed
     *
     */
    @Test
    public void testFindAll() throws XMLRepositoryAdoptionException {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        Adoption adoption2 = new Adoption("50002",2L,2L,2020);
        adoption1.setId(ID);
        adoption2.setId(ID+1);
        assertFalse(adoptionRepository.save(adoption1).isPresent());
        assertFalse(adoptionRepository.save(adoption2).isPresent());
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
     */
    @Test
    public void testSave() {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        try{
            adoptionRepository.save(null);
            fail();
        }catch(IllegalArgumentException e){ }

        adoption1.setId(ID+100);

        try{
            adoption1.setAdoptionYear(-200);
            adoptionRepository.save(adoption1);
            fail();
        }catch(ValidatorException e){
            adoption1.setAdoptionYear(2019);
        }

        assertFalse(adoptionRepository.save(adoption1).isPresent());
        assertTrue(adoptionRepository.findOne(ID+100).isPresent());

        Adoption adoption2 = new Adoption("50002",2L,2L,2020);
        adoption2.setId(ID+100);
        assertTrue(adoptionRepository.save(adoption2).isPresent());

        adoptionRepository.delete(adoption1.getId());
    }

    /**
     * Tests whether a adoption can be removed from the repository.
     * (should result in no exceptions)
     *
     */
    @Test
    public void testDelete() {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        adoption1.setId(ID);
        assertFalse(adoptionRepository.save(adoption1).isPresent());
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        assertTrue(adoptionRepository.delete(ID).isPresent());
        assertFalse(adoptionRepository.findOne(ID).isPresent());
        assertFalse(adoptionRepository.delete(ID).isPresent());

        try{
            adoptionRepository.delete(null);
            fail();
        }catch(IllegalArgumentException e) {}
    }

    /**
     * Tests whether a adoption can be removed from the repository.
     * (should result in an exception)
     *
     * @throws IllegalArgumentException
     * if the delete did not succeed
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteException() throws IllegalArgumentException {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        adoptionRepository.delete(null);
    }

    /**
     * Tests whether a adoption can be updated in the repository.
     * A new adoption is created and updated in the repository.
     * Then the old adoption is retrieved from the repository (should not be found)
     * and the new adoption is retrieved from the repository as well (should be found)
     *
     */
    @Test
    public void testUpdate() {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
        Adoption adoption1 = new Adoption("50001",1L,1L,2019);
        adoption1.setId(ID);
        assertFalse(adoptionRepository.save(adoption1).isPresent());
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        Adoption adoption2 = new Adoption("50002",2L,2L,2020);
        adoption2.setId(ID);
        assertFalse(adoptionRepository.update(adoption2).isPresent()); // should succeed
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        adoptionRepository.delete(ID);
    }

    /**
     * Tests whether a adoption can be updated in the repository.
     * (should result in an exception)
     *
     * @throws ValidatorException
     * if the update did not succeed
     */
    @Test(expected = ValidatorException.class)
    public void testUpdateException() throws ValidatorException {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
        try{
            adoptionRepository.update(null);
            fail();
        }catch(IllegalArgumentException e) {}

        Adoption adoption = new Adoption("50001",1L,1L,2019);
        adoption.setId(ID);
        adoption.setAdoptionYear(-100);
        adoptionRepository.update(adoption);
    }

    /**
     * Tests whether the file name was retrieved correctly
     */
    @Test
    public void getFileName() {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "testFileName");
        assertSame("testFileName", ((XMLRepositoryAdoption) adoptionRepository).getFileName());
    }

    /**
     * Tests whether the file name was set correctly
     */
    @Test
    public void setFileName() {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "testFileName");
        ((XMLRepositoryAdoption) adoptionRepository).setFileName("testFileName1");
        assertSame("testFileName1", ((XMLRepositoryAdoption) adoptionRepository).getFileName());
    }

    /**
     * Tests whether an adoption can be saved to xml format
     *
     */
    @Test
    public void saveToXML() {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
        Adoption adoption1 = new Adoption("50001",1L,1L,2018);
        adoption1.setId(ID);
        ((XMLRepositoryAdoption)adoptionRepository).saveToXML(adoption1);
        assertTrue(adoptionRepository.delete(adoption1.getId()).isPresent());
    }

    /**
     * Tests whether the repository can remove all adoption instances from the xml file
     *
     */
    @Test
    public void deleteAll() {
        adoptionRepository = new XMLRepositoryAdoption<>(adoptionValidator, "test/adoptionsTest");
        ((XMLRepositoryAdoption)adoptionRepository).deleteAll();
    }

    /**
     * Tests whether more adoptions can be saved to xml format (from a list)
     *
     */
    @Test
    public void saveEntitiesToXML() {
        List<Adoption> adoptions = new ArrayList<>();
        Adoption adoption1 = new Adoption("50001",1L,1L,2018);
        Adoption adoption2 = new Adoption("50002",2L,2L,2019);
        Adoption adoption3 = new Adoption("50003",3L,3L,2020);
        adoption1.setId(ID);
        adoption2.setId(ID+1);
        adoption3.setId(ID+2);
        adoptions.add(adoption1);
        adoptions.add(adoption2);
        adoptions.add(adoption3);
        ((XMLRepositoryAdoption)adoptionRepository).saveEntitiesToXML(adoptions);
        ((XMLRepositoryAdoption)adoptionRepository).deleteAll();
    }

    /**
     * Tests whether more adoptions can be saved to xml format (from a map)
     *
     */
    @Test
    public void saveMapToXML() {
        Map<Long, Adoption> adoptions = new HashMap<>();
        Adoption adoption1 = new Adoption("50001",1L,1L,2018);
        Adoption adoption2 = new Adoption("50002",2L,2L,2019);
        Adoption adoption3 = new Adoption("50003",3L,3L,2020);
        adoption1.setId(ID);
        adoption2.setId(ID+1);
        adoption3.setId(ID+2);
        adoptions.put(adoption1.getId(), adoption1);
        adoptions.put(adoption2.getId(), adoption2);
        adoptions.put(adoption3.getId(), adoption3);
        ((XMLRepositoryAdoption)adoptionRepository).saveMapToXML(adoptions);
        ((XMLRepositoryAdoption)adoptionRepository).deleteAll();
    }
}