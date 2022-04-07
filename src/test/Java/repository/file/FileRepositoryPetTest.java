package repository.file;
import domain.Pet.Pet;
import domain.validators.*;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;
import repository.XML.exceptions.XMLRepositoryPetException;
import repository.file.exceptions.FileRepositoryPetException;

import java.io.*;
import java.util.Iterator;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FileRepositoryPetTest {
    private static final Long ID = new Long(1);

    private Validator<Pet> PetValidator;
    private Repository<Long, Pet> PetRepository;


    @BeforeEach
    public void setup() throws Exception {
        PetValidator = new PetValidator();
        PetRepository = new FileRepositoryPet(PetValidator, "data/file/test/petsTest.csv");

        Pet first = new Pet("3333", "Antonia", "caine", 2000);
        first.setId(1L);
        Pet second = new Pet("4444", "Maria", "pisica", 2021);
        second.setId(2L);
        Pet third = new Pet("5555", "Geta", "vulpe", 2014);
        third.setId(3L);
        Pet forth = new Pet("6666", "Sonia", "pasare", 2010);
        forth.setId(4L);
    }

    @AfterEach
    public void teardown() throws Exception {
        PetValidator = null;
        PetRepository = null;
        new FileWriter("data/file/test/petsTest.csv");
    }


    @Test
    public void testGetFileName() {
        assertSame("data/file/test/petsTest.csv", ((FileRepositoryPet) PetRepository).getFileName());
    }

    @Test
    public void setFileName() {
        ((FileRepositoryPet) PetRepository).setFileName("Ana are mere");
        assertSame("Ana are mere", ((FileRepositoryPet) PetRepository).getFileName());
    }

    /**
     * Test the reading process into the file.
     * A Pet is created, saved into the file and then the file is read.
     *
     * @throws FileRepositoryPetException if the file can't be opened
     */
    @Test
    public void testSaveToFile() throws FileRepositoryPetException {
/*        Pet first = new Pet("3333", 1L, 2L, 2000);
        first.setId(1L);
        ((FileRepositoryPet)PetRepository).saveToFile(first);

        try(Stream<String> stream = Files.lines(Paths.get("data/file/test/PetsTest.csv"))) {

        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /**
     * Test whether the Pet is converted without faults into a comma separated string.
     *
     * @throws FileRepositoryPetException
     */
    @Test
    public void testGetStringFromPet() throws FileRepositoryPetException {
        Pet first = new Pet("3333", "Antonia", "caine", 2000);
        first.setId(1L);
        String supposed_result = "1,3333,Antonia,caine,2000";
        assertTrue(((FileRepositoryPet) PetRepository).getStringFromPet(first).equals(supposed_result));

    }

/*    @Test
    public void testSaveEntitiesToFile() throws FileRepositoryPetException {
        Pet first = new Pet("3333", 1L, 2L, 2000);
        first.setId(1L);
        Pet second = new Pet("4444", 2L, 3L, 2021);
        second.setId(2L);

        List<Pet> Pets = new ArrayList<Pet>();
        Pets.add(first);
        Pets.add(second);

        //((FileRepositoryPet) PetRepository).save(first);
        //((FileRepositoryPet) PetRepository).save(second);
        ((FileRepositoryPet) PetRepository).saveEntitiesToFile(Pets);
        Path filename = Path.of("data/file/test/PetsTest.csv");

        try {
            String content = Files.readString(filename);
            assertTrue(content.equals("1,3333,1,2,2000\n2,4444,2,3,2021"));

        } catch (IOException error) {
            throw new FileRepositoryPetException(error);
        }

    }*/

    @Test
    public void testReadFile() throws FileRepositoryPetException {

    }

    /**
     * Tests finding a Pet from the repository.
     * A Pet is created, saved in the repository and then retrieved.
     *
     * @throws XMLRepositoryPetException if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws FileRepositoryPetException {
        Pet first = new Pet("3333", "Antonia", "caine", 2000);
        first.setId(1L);
        PetRepository.save(first);
        assertTrue(PetRepository.findOne(ID).isPresent());
        assertTrue(PetRepository.delete(ID).isPresent());
        assertFalse(PetRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether the method find all from the repository is getting out the right list of entities.
     * I'm adding 2 entities into repo.
     * Then call findall which should return an Iterable object that contains 2 elements.
     *
     * @throws FileRepositoryPetException
     */
    @Test
    public void testFindAll() throws FileRepositoryPetException {
        Pet first = new Pet("3333", "Antonia", "caine", 2000);
        first.setId(1L);
        Pet second = new Pet("4444", "Maria", "pisica", 2021);
        second.setId(2L);
        this.PetRepository.save(first);
        this.PetRepository.save(second);

        Iterable<Pet> iterable = this.PetRepository.findAll();
        Iterator<Pet> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(this.PetRepository.delete(first.getId()).isPresent());
        assertTrue(this.PetRepository.delete(second.getId()).isPresent());
    }

    /**
     * Tests whether a Pet can be saved in the repository.
     * (should result in no exceptions)
     *
     * @throws FileRepositoryPetException if the save operation did not succeed
     */
    @Test
    public void testSave() throws FileRepositoryPetException {
        Pet first = new Pet("3333", "Antonia", "caine", 2000);
        first.setId(1L);
        Pet second = new Pet("4444", "Maria", "pisica", 2021);
        second.setId(2L);
        Pet third = new Pet("5555", "Geta", "vulpe", 2014);
        third.setId(3L);
        Pet forth = new Pet("6666", "Sonia", "pasare", 2010);
        forth.setId(4L);

        Assert.assertFalse(PetRepository.save(first).isPresent());
        Assert.assertFalse(PetRepository.save(second).isPresent());
        Assert.assertFalse(PetRepository.save(third).isPresent());
        Assert.assertFalse(PetRepository.save(forth).isPresent());
        Assert.assertTrue(PetRepository.save(forth).isPresent());

        assertTrue(PetRepository.findOne(first.getId()).isPresent());
        assertTrue(PetRepository.findOne(second.getId()).isPresent());
        assertTrue(PetRepository.findOne(third.getId()).isPresent());
        assertTrue(PetRepository.findOne(forth.getId()).isPresent());

    }

    /**
     * Tests wheter an Pet can be deleted from the repository.
     * A Pet is created, added into repo, searched to see if
     * it's actually there and then deleted.
     * Before delete another find is performed and the Pet should
     * not exist anymore.
     *
     * @throws FileRepositoryPetException
     */
    @Test
    public void testDelete() throws FileRepositoryPetException {
        Pet first = new Pet("3333", "Antonia", "caine", 2000);
        first.setId(1L);
        PetRepository.save(first);
        assertTrue(PetRepository.findOne(1L).isPresent());
        assertTrue(PetRepository.delete(1L).isPresent());
        assertFalse(PetRepository.findOne(1L).isPresent());
    }

    /**
     * @throws FileRepositoryPetException
     */
    @Test
    public void testUpdate() throws FileRepositoryPetException {
        Pet first = new Pet("3333", "Antonia", "caine", 2000);
        first.setId(1L);
        assertFalse(PetRepository.save(first).isPresent());
        assertTrue(PetRepository.findOne(1L).isPresent());

        Pet second = new Pet("4444", "Maria", "pisica", 2021);
        second.setId(1L);
        assertFalse(PetRepository.update(second).isPresent());
        assertTrue(PetRepository.findOne(1L).isPresent());

        Pet forth = new Pet("6666", "Sonia", "pasare", 2010);
        forth.setId(4L);
        assertTrue(PetRepository.update(forth).isPresent());
    }
}
