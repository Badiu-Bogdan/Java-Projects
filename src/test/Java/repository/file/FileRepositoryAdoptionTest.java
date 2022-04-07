package repository.file;

import domain.Adoption.Adoption;
import domain.validators.AdoptionValidator;
import domain.validators.Validator;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryAdoptionException;
import repository.file.exceptions.FileRepositoryAdoptionException;

import java.io.*;
import java.util.Iterator;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FileRepositoryAdoptionTest {
    private static final Long ID = new Long(1);

    private Validator<Adoption> adoptionValidator;
    private Repository<Long, Adoption> adoptionRepository;


    @BeforeEach
    public void setup() throws Exception {
        adoptionValidator = new AdoptionValidator();
        adoptionRepository = new FileRepositoryAdoption(adoptionValidator, "data/file/test/adoptionsTest.csv");

        Adoption first = new Adoption("3333", 1L, 2L, 2000);
        first.setId(1L);
        Adoption second = new Adoption("4444", 2L, 3L, 2021);
        second.setId(2L);
        Adoption third = new Adoption("5555", 3L, 4L, 2014);
        third.setId(3L);
        Adoption forth = new Adoption("6666", 4L, 5L, 2010);
        forth.setId(4L);
    }

    @AfterEach
    public void teardown() throws Exception {
        adoptionValidator = null;
        adoptionRepository = null;
        new FileWriter("data/file/test/adoptionsTest.csv");
    }


    @Test
    public void testGetFileName() {
        assertSame("data/file/test/adoptionsTest.csv", ((FileRepositoryAdoption) adoptionRepository).getFileName());
    }

    @Test
    public void setFileName() {
        ((FileRepositoryAdoption) adoptionRepository).setFileName("Ana are mere");
        assertSame("Ana are mere", ((FileRepositoryAdoption) adoptionRepository).getFileName());
    }

    /**
     * Test the reading process into the file.
     * A adoption is created, saved into the file and then the file is read.
     *
     * @throws FileRepositoryAdoptionException if the file can't be opened
     */
    @Test
    public void testSaveToFile() throws FileRepositoryAdoptionException {
/*        Adoption first = new Adoption("3333", 1L, 2L, 2000);
        first.setId(1L);
        ((FileRepositoryAdoption)adoptionRepository).saveToFile(first);

        try(Stream<String> stream = Files.lines(Paths.get("data/file/test/adoptionsTest.csv"))) {

        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /**
     * Test whether the Adoption is converted without faults into a comma separated string.
     *
     * @throws FileRepositoryAdoptionException
     */
    @Test
    public void testGetStringFromAdoption() throws FileRepositoryAdoptionException {
        Adoption first = new Adoption("3333", 1L, 2L, 2000);
        first.setId(1L);
        String supposed_result = "1,3333,1,2,2000";
        assertTrue(((FileRepositoryAdoption) adoptionRepository).getStringFromAdoption(first).equals(supposed_result));

    }

/*    @Test
    public void testSaveEntitiesToFile() throws FileRepositoryAdoptionException {
        Adoption first = new Adoption("3333", 1L, 2L, 2000);
        first.setId(1L);
        Adoption second = new Adoption("4444", 2L, 3L, 2021);
        second.setId(2L);

        List<Adoption> adoptions = new ArrayList<Adoption>();
        adoptions.add(first);
        adoptions.add(second);

        //((FileRepositoryAdoption) adoptionRepository).save(first);
        //((FileRepositoryAdoption) adoptionRepository).save(second);
        ((FileRepositoryAdoption) adoptionRepository).saveEntitiesToFile(adoptions);
        Path filename = Path.of("data/file/test/adoptionsTest.csv");

        try {
            String content = Files.readString(filename);
            assertTrue(content.equals("1,3333,1,2,2000\n2,4444,2,3,2021"));

        } catch (IOException error) {
            throw new FileRepositoryAdoptionException(error);
        }

    }*/

    @Test
    public void testReadFile() throws FileRepositoryAdoptionException {

    }

    /**
     * Tests finding a adoption from the repository.
     * A adoption is created, saved in the repository and then retrieved.
     *
     * @throws XMLRepositoryAdoptionException if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws FileRepositoryAdoptionException {
        Adoption first = new Adoption("3333", 1L, 2L, 2000);
        first.setId(1L);
        adoptionRepository.save(first);
        assertTrue(adoptionRepository.findOne(ID).isPresent());
        assertTrue(adoptionRepository.delete(ID).isPresent());
        assertFalse(adoptionRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether the method find all from the repository is getting out the right list of entities.
     * I'm adding 2 entities into repo.
     * Then call findall which should return an Iterable object that contains 2 elements.
     *
     * @throws FileRepositoryAdoptionException
     */
    @Test
    public void testFindAll() throws FileRepositoryAdoptionException {
        Adoption first = new Adoption("3333", 1L, 2L, 2000);
        first.setId(1L);
        Adoption second = new Adoption("4444", 2L, 3L, 2021);
        second.setId(2L);
        this.adoptionRepository.save(first);
        this.adoptionRepository.save(second);

        Iterable<Adoption> iterable = this.adoptionRepository.findAll();
        Iterator<Adoption> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(this.adoptionRepository.delete(first.getId()).isPresent());
        assertTrue(this.adoptionRepository.delete(second.getId()).isPresent());
    }

    /**
     * Tests whether a adoption can be saved in the repository.
     * (should result in no exceptions)
     *
     * @throws FileRepositoryAdoptionException if the save operation did not succeed
     */
    @Test
    public void testSave() throws FileRepositoryAdoptionException {
        Adoption first = new Adoption("3333", 1L, 2L, 2000);
        first.setId(1L);
        Adoption second = new Adoption("4444", 2L, 3L, 2021);
        second.setId(2L);
        Adoption third = new Adoption("5555", 3L, 4L, 2014);
        third.setId(3L);
        Adoption forth = new Adoption("6666", 4L, 5L, 2010);
        forth.setId(4L);

        Assert.assertFalse(adoptionRepository.save(first).isPresent());
        Assert.assertFalse(adoptionRepository.save(second).isPresent());
        Assert.assertFalse(adoptionRepository.save(third).isPresent());
        Assert.assertFalse(adoptionRepository.save(forth).isPresent());
        Assert.assertTrue(adoptionRepository.save(forth).isPresent());

        assertTrue(adoptionRepository.findOne(first.getId()).isPresent());
        assertTrue(adoptionRepository.findOne(second.getId()).isPresent());
        assertTrue(adoptionRepository.findOne(third.getId()).isPresent());
        assertTrue(adoptionRepository.findOne(forth.getId()).isPresent());

    }

    /**
     * Tests wheter an adoption can be deleted from the repository.
     * A adoption is created, added into repo, searched to see if
     * it's actually there and then deleted.
     * Before delete another find is performed and the adoption should
     * not exist anymore.
     *
     * @throws FileRepositoryAdoptionException
     */
    @Test
    public void testDelete() throws FileRepositoryAdoptionException {
        Adoption first = new Adoption("3333", 1L, 2L, 2000);
        first.setId(1L);
        adoptionRepository.save(first);
        assertTrue(adoptionRepository.findOne(1L).isPresent());
        assertTrue(adoptionRepository.delete(1L).isPresent());
        assertFalse(adoptionRepository.findOne(1L).isPresent());
    }

    /**
     * @throws FileRepositoryAdoptionException
     */
    @Test
    public void testUpdate() throws FileRepositoryAdoptionException {
        Adoption first = new Adoption("3333", 1L, 2L, 2000);
        first.setId(1L);
        assertFalse(adoptionRepository.save(first).isPresent());
        assertTrue(adoptionRepository.findOne(1L).isPresent());


        Adoption second = new Adoption("4444", 2L, 3L, 2021);
        second.setId(1L);
        assertFalse(adoptionRepository.update(second).isPresent());
        assertTrue(adoptionRepository.findOne(1L).isPresent());

        Adoption forth = new Adoption("6666", 4L, 5L, 2010);
        forth.setId(4L);
        assertTrue(adoptionRepository.update(forth).isPresent());
    }
}
