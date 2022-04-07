package repository.file;
import domain.Purchase.Purchase;
import domain.validators.PurchaseValidator;

import domain.validators.Validator;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;
import repository.file.exceptions.FileRepositoryStoreException;

import java.io.*;
import java.util.Iterator;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FileRepositoryPurchaseTest {
    private static final Long ID = new Long(1);

    private Validator<Purchase> PurchaseValidator;
    private Repository<Long, Purchase> PurchaseRepository;


    @BeforeEach
    public void setup() throws Exception {
        PurchaseValidator = new PurchaseValidator();
        PurchaseRepository = new FileRepositoryStore(PurchaseValidator, "data/file/test/purchasesTest.csv");

        Purchase first = new Purchase("3333", 1L, 2L, 2000);
        first.setId(1L);
        Purchase second = new Purchase("4444", 2L, 3L, 2021);
        second.setId(2L);
        Purchase third = new Purchase("5555", 3L, 4L, 2014);
        third.setId(3L);
        Purchase forth = new Purchase("6666", 4L, 5L, 2010);
        forth.setId(4L);
    }

    @AfterEach
    public void teardown() throws Exception {
        PurchaseValidator = null;
        PurchaseRepository = null;
        new FileWriter("data/file/test/purchasesTest.csv");
    }


    @Test
    public void testGetFileName() {
        assertSame("data/file/test/purchasesTest.csv", ((FileRepositoryStore) PurchaseRepository).getFileName());
    }

    @Test
    public void setFileName() {
        ((FileRepositoryStore) PurchaseRepository).setFileName("Ana are mere");
        assertSame("Ana are mere", ((FileRepositoryStore) PurchaseRepository).getFileName());
    }

    /**
     * Test the reading process into the file.
     * A Purchase is created, saved into the file and then the file is read.
     *
     * @throws FileRepositoryStoreException if the file can't be opened
     */
    @Test
    public void testSaveToFile() throws FileRepositoryStoreException {
/*        Purchase first = new Purchase("3333", 1L, 2L, 2000);
        first.setId(1L);
        ((FileRepositoryStore)PurchaseRepository).saveToFile(first);

        try(Stream<String> stream = Files.lines(Paths.get("data/file/test/PurchasesTest.csv"))) {

        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /**
     * Test whether the Purchase is converted without faults into a comma separated string.
     *
     * @throws FileRepositoryStoreException
     */
    @Test
    public void testGetStringFromPurchase() throws FileRepositoryStoreException {
        Purchase first = new Purchase("3333", 1L, 2L, 2000);
        first.setId(1L);
        String supposed_result = "1,3333,1,2,2000";
        assertTrue(((FileRepositoryStore) PurchaseRepository).getStringFromPurchase(first).equals(supposed_result));

    }

/*    @Test
    public void testSaveEntitiesToFile() throws FileRepositoryStoreException {
        Purchase first = new Purchase("3333", 1L, 2L, 2000);
        first.setId(1L);
        Purchase second = new Purchase("4444", 2L, 3L, 2021);
        second.setId(2L);

        List<Purchase> Purchases = new ArrayList<Purchase>();
        Purchases.add(first);
        Purchases.add(second);

        //((FileRepositoryStore) PurchaseRepository).save(first);
        //((FileRepositoryStore) PurchaseRepository).save(second);
        ((FileRepositoryStore) PurchaseRepository).saveEntitiesToFile(Purchases);
        Path filename = Path.of("data/file/test/PurchasesTest.csv");

        try {
            String content = Files.readString(filename);
            assertTrue(content.equals("1,3333,1,2,2000\n2,4444,2,3,2021"));

        } catch (IOException error) {
            throw new FileRepositoryStoreException(error);
        }

    }*/

    @Test
    public void testReadFile() throws FileRepositoryStoreException {

    }

    /**
     * Tests finding a Purchase from the repository.
     * A Purchase is created, saved in the repository and then retrieved.
     *
     * @throws FileRepositoryStoreException if the findOne operation did not succeed
     */
    @Test
    public void testFindOne() throws FileRepositoryStoreException {
        Purchase first = new Purchase("3333", 1L, 2L, 2000);
        first.setId(1L);
        PurchaseRepository.save(first);
        assertTrue(PurchaseRepository.findOne(ID).isPresent());
        assertTrue(PurchaseRepository.delete(ID).isPresent());
        assertFalse(PurchaseRepository.findOne(ID).isPresent());

    }

    /**
     * Tests whether the method find all from the repository is getting out the right list of entities.
     * I'm adding 2 entities into repo.
     * Then call findall which should return an Iterable object that contains 2 elements.
     *
     * @throws FileRepositoryStoreException
     */
    @Test
    public void testFindAll() throws FileRepositoryStoreException {
        Purchase first = new Purchase("3333", 1L, 2L, 2000);
        first.setId(1L);
        Purchase second = new Purchase("4444", 2L, 3L, 2021);
        second.setId(2L);
        this.PurchaseRepository.save(first);
        this.PurchaseRepository.save(second);

        Iterable<Purchase> iterable = this.PurchaseRepository.findAll();
        Iterator<Purchase> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(this.PurchaseRepository.delete(first.getId()).isPresent());
        assertTrue(this.PurchaseRepository.delete(second.getId()).isPresent());
    }

    /**
     * Tests whether a Purchase can be saved in the repository.
     * (should result in no exceptions)
     *
     * @throws FileRepositoryStoreException if the save operation did not succeed
     */
    @Test
    public void testSave() throws FileRepositoryStoreException {
        Purchase first = new Purchase("3333", 1L, 2L, 2000);
        first.setId(1L);
        Purchase second = new Purchase("4444", 2L, 3L, 2021);
        second.setId(2L);
        Purchase third = new Purchase("5555", 3L, 4L, 2014);
        third.setId(3L);
        Purchase forth = new Purchase("6666", 4L, 5L, 2010);
        forth.setId(4L);

        Assert.assertFalse(PurchaseRepository.save(first).isPresent());
        Assert.assertFalse(PurchaseRepository.save(second).isPresent());
        Assert.assertFalse(PurchaseRepository.save(third).isPresent());
        Assert.assertFalse(PurchaseRepository.save(forth).isPresent());
        Assert.assertTrue(PurchaseRepository.save(forth).isPresent());

        assertTrue(PurchaseRepository.findOne(first.getId()).isPresent());
        assertTrue(PurchaseRepository.findOne(second.getId()).isPresent());
        assertTrue(PurchaseRepository.findOne(third.getId()).isPresent());
        assertTrue(PurchaseRepository.findOne(forth.getId()).isPresent());

    }

    /**
     * Tests wheter an Purchase can be deleted from the repository.
     * A Purchase is created, added into repo, searched to see if
     * it's actually there and then deleted.
     * Before delete another find is performed and the Purchase should
     * not exist anymore.
     *
     * @throws FileRepositoryStoreException
     */
    @Test
    public void testDelete() throws FileRepositoryStoreException {
        Purchase first = new Purchase("3333", 1L, 2L, 2000);
        first.setId(1L);
        PurchaseRepository.save(first);
        assertTrue(PurchaseRepository.findOne(1L).isPresent());
        assertTrue(PurchaseRepository.delete(1L).isPresent());
        assertFalse(PurchaseRepository.findOne(1L).isPresent());
    }

    /**
     * @throws FileRepositoryStoreException
     */
    @Test
    public void testUpdate() throws FileRepositoryStoreException {
        Purchase first = new Purchase("3333", 1L, 2L, 2000);
        first.setId(1L);
        assertFalse(PurchaseRepository.save(first).isPresent());
        assertTrue(PurchaseRepository.findOne(1L).isPresent());

        Purchase second = new Purchase("4444", 2L, 3L, 2021);
        second.setId(1L);
        assertFalse(PurchaseRepository.update(second).isPresent());
        assertTrue(PurchaseRepository.findOne(1L).isPresent());

        Purchase forth = new Purchase("6666", 4L, 5L, 2010);
        forth.setId(4L);
        assertTrue(PurchaseRepository.update(forth).isPresent());
    }
}
