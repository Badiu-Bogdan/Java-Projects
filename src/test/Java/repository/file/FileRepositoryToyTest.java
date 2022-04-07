package repository.file;

import domain.Toy.Toy;
import domain.validators.ToyValidator;
import domain.validators.Validator;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryToyException;
import repository.file.exceptions.FileRepositoryToyException;

import java.io.*;
import java.util.Iterator;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FileRepositoryToyTest {
    private static final Long ID = new Long(1);

    private Validator<Toy> toyValidator;
    private Repository<Long, Toy> toyRepository;


    @BeforeEach
    public void setup() throws Exception {
        toyValidator = new ToyValidator();
        toyRepository = new FileRepositoryToy(toyValidator, "data/file/test/toysTest.csv");

        Toy first = new Toy("50001", "name1", 100, "material1", 1.99);
        first.setId(1L);
        Toy second = new Toy("50002", "name2", 200, "material2", 2.99);
        second.setId(2L);
        Toy third = new Toy("50003", "name3", 300, "material3", 3.99);
        third.setId(3L);
        Toy forth = new Toy("6666", "name4", 400, "material4", 4.99);
        forth.setId(4L);
    }

    @AfterEach
    public void teardown() throws Exception {
        toyValidator = null;
        toyRepository = null;
        new FileWriter("data/file/test/toysTest.csv");
    }


    @Test
    public void testGetFileName() {
        assertSame("data/file/test/toysTest.csv", ((FileRepositoryToy) toyRepository).getFileName());
    }

    @Test
    public void setFileName() {
        ((FileRepositoryToy) toyRepository).setFileName("Ana are mere");
        assertSame("Ana are mere", ((FileRepositoryToy) toyRepository).getFileName());
    }

    /**
     * Test the reading process into the file.
     * A toy is created, saved into the file and then the file is read.
     *
     * @throws FileRepositoryToyException if the file can't be opened
     */
    @Test
    public void testSaveToFile() throws FileRepositoryToyException {
/*        Toy first = new Toy("50001", "name1", 100, "material1", 1.99);
        first.setId(1L);
        ((FileRepositoryToy)toyRepository).saveToFile(first);

        try(Stream<String> stream = Files.lines(Paths.get("data/file/test/toysTest.csv"))) {

        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /**
     * Test whether the Toy is converted without faults into a comma separated string.
     *
     * @throws FileRepositoryToyException
     */
    @Test
    public void testGetStringFromToy() throws FileRepositoryToyException {
        Toy first = new Toy("50001", "name1", 100, "material1", 1.99);
        first.setId(1L);
        String supposed_result = "1,50001,name1,100,material1,1.99";
        assertTrue(((FileRepositoryToy) toyRepository).getStringFromToy(first).equals(supposed_result));

    }

/*    @Test
    public void testSaveEntitiesToFile() throws FileRepositoryToyException {
        Toy first = new Toy("50001", "name1", 100, "material1", 1.99);
        first.setId(1L);
        Toy second = new Toy("50002", "name2", 200, "material2", 2.99);
        second.setId(2L);

        List<Toy> toys = new ArrayList<Toy>();
        toys.add(first);
        toys.add(second);

        //((FileRepositoryToy) toyRepository).save(first);
        //((FileRepositoryToy) toyRepository).save(second);
        ((FileRepositoryToy) toyRepository).saveEntitiesToFile(toys);
        Path filename = Path.of("data/file/test/toysTest.csv");

        try {
            String content = Files.readString(filename);
            assertTrue(content.equals("1,3333,1,2,2000\n2,4444,2,3,2021"));

        } catch (IOException error) {
            throw new FileRepositoryToyException(error);
        }

    }*/

    @Test
    public void testReadFile() throws FileRepositoryToyException {

    }

    /**
     * Tests finding a toy from the repository.
     * A toy is created, saved in the repository and then retrieved.
     *
     * @throws XMLRepositoryToyException if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws FileRepositoryToyException {
        Toy first = new Toy("50001", "name1", 100, "material1", 1.99);
        first.setId(1L);
        toyRepository.save(first);
        assertTrue(toyRepository.findOne(ID).isPresent());
        assertTrue(toyRepository.delete(ID).isPresent());
        assertFalse(toyRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether the method find all from the repository is getting out the right list of entities.
     * I'm adding 2 entities into repo.
     * Then call findall which should return an Iterable object that contains 2 elements.
     *
     * @throws FileRepositoryToyException
     */
    @Test
    public void testFindAll() throws FileRepositoryToyException {
        Toy first = new Toy("50001", "name1", 100, "material1", 1.99);
        first.setId(1L);
        Toy second = new Toy("50002", "name2", 200, "material2", 2.99);
        second.setId(2L);
        this.toyRepository.save(first);
        this.toyRepository.save(second);

        Iterable<Toy> iterable = this.toyRepository.findAll();
        Iterator<Toy> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(this.toyRepository.delete(first.getId()).isPresent());
        assertTrue(this.toyRepository.delete(second.getId()).isPresent());
    }

    /**
     * Tests whether a toy can be saved in the repository.
     * (should result in no exceptions)
     *
     * @throws FileRepositoryToyException if the save operation did not succeed
     */
    @Test
    public void testSave() throws FileRepositoryToyException {
        Toy first = new Toy("50001", "name1", 100, "material1", 1.99);
        first.setId(1L);
        Toy second = new Toy("50002", "name2", 200, "material2", 2.99);
        second.setId(2L);
        Toy third = new Toy("50003", "name3", 300, "material3", 3.99);
        third.setId(3L);
        Toy forth = new Toy("6666", "name4", 400, "material4", 4.99);
        forth.setId(4L);

        Assert.assertFalse(toyRepository.save(first).isPresent());
        Assert.assertFalse(toyRepository.save(second).isPresent());
        Assert.assertFalse(toyRepository.save(third).isPresent());
        Assert.assertFalse(toyRepository.save(forth).isPresent());
        Assert.assertTrue(toyRepository.save(forth).isPresent());

        assertTrue(toyRepository.findOne(first.getId()).isPresent());
        assertTrue(toyRepository.findOne(second.getId()).isPresent());
        assertTrue(toyRepository.findOne(third.getId()).isPresent());
        assertTrue(toyRepository.findOne(forth.getId()).isPresent());

    }

    /**
     * Tests wheter an toy can be deleted from the repository.
     * A toy is created, added into repo, searched to see if
     * it's actually there and then deleted.
     * Before delete another find is performed and the toy should
     * not exist anymore.
     *
     * @throws FileRepositoryToyException
     */
    @Test
    public void testDelete() throws FileRepositoryToyException {
        Toy first = new Toy("50001", "name1", 100, "material1", 1.99);
        first.setId(1L);
        toyRepository.save(first);
        assertTrue(toyRepository.findOne(1L).isPresent());
        assertTrue(toyRepository.delete(1L).isPresent());
        assertFalse(toyRepository.findOne(1L).isPresent());
    }

    /**
     * @throws FileRepositoryToyException
     */
    @Test
    public void testUpdate() throws FileRepositoryToyException {
        Toy first = new Toy("50001", "name1", 100, "material1", 1.99);
        first.setId(1L);
        assertFalse(toyRepository.save(first).isPresent());
        assertTrue(toyRepository.findOne(1L).isPresent());

        Toy second = new Toy("50002", "name2", 200, "material2", 2.99);
        second.setId(1L);
        toyRepository.update(second);
        assertTrue(toyRepository.findOne(1L).isPresent());

        Toy forth = new Toy("6666", "name4", 400, "material4", 4.99);
        forth.setId(4L);
        assertTrue(toyRepository.update(forth).isPresent());
    }
}
