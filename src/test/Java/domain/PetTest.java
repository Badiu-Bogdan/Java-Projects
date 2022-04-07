package domain;


import domain.Pet.Pet;
//import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PetTest {
    private static final Long ID = new Long(1);
    private static final Long NEW_ID = new Long(2);
    private static final String SERIAL_NUMBER = "001";
    private static final String NEW_SERIAL_NUMBER = "007";
    private static final String NAME = "PetName1";
    private static final String NEW_NAME = "PetName2";
    private static final String BREED = "PetBreed1";
    private static final String NEW_BREED = "PetBreed2";
    private static final int BIRTH_YEAR = 2018;
    private static final int NEW_BIRTH_YEAR = 2017;

    private Pet pet;

    @BeforeEach
    public void SetUp() throws Exception {
        pet = new Pet(SERIAL_NUMBER,NAME,BREED,BIRTH_YEAR);
        pet.setId(ID);
    }

    @AfterEach
    public void TearDown() throws Exception {
        pet=null;
    }

    /**
     * Tests whether the retrieved serial number is equal to the specified one in the constructor
     *
     * @throws Exception
     * if serial numbers are not equal
     */
    @Test
    public void testGetSerialNumber() throws Exception {
        assertEquals("Serial numbers should be equal", SERIAL_NUMBER, pet.getSerialNumber());
    }

    /**
     * Tests whether the freshly set serial number can be retrieved
     *
     * @throws Exception
     * if the new serial number was not properly set
     */
    @Test
    public void testSetSerialNumber() throws Exception {
        pet.setSerialNumber(NEW_SERIAL_NUMBER);
        assertEquals("Serial numbers should be equal", NEW_SERIAL_NUMBER, pet.getSerialNumber());
    }

    /**
     * Tests whether the retrieved id is equal to the specified one in the constructor
     *
     * @throws Exception
     * if ids are not equal
     */
    @Test
    public void testGetId() throws Exception {
        assertEquals("Ids should be equal", ID, pet.getId());
    }

    /**
     * Tests whether the freshly set id can be retrieved
     *
     * @throws Exception
     * if the new id was not properly set
     */
    @Test
    public void testSetId() throws Exception {
        pet.setId(NEW_ID);
        assertEquals("Ids should be equal", NEW_ID, pet.getId());
    }

    /**
     * Tests whether the retrieved name is equal to the specified one in the constructor
     *
     * @throws Exception
     * if the names are not equal
     */
    @Test
    public void testGetName() throws Exception {
        assertEquals("Names should be equal", NAME, pet.getName());
    }

    /**
     * Tests whether the freshly set name can be retrieved
     *
     * @throws Exception
     * if the new name was not properly set
     */
    @Test
    public void testSetName() throws Exception {
        pet.setName(NEW_NAME);
        assertEquals("Names should be equal", NEW_NAME, pet.getName());
    }

    /**
     * Tests whether the retrieved breed is equal to the specified one in the constructor
     *
     * @throws Exception
     * if breeds are not equal
     */
    @Test
    public void testGetBreed() throws Exception {
        assertEquals("Breeds should be equal", BREED, pet.getBreed());
    }

    /**
     * Tests whether the freshly set breed can be retrieved
     *
     * @throws Exception
     * if the new breed value was not properly set
     */
    @Test
    public void testSetBreed() throws Exception {
        pet.setBreed(NEW_BREED);
        assertEquals("Breeds should be equal", NEW_BREED, pet.getBreed());
    }

    /**
     * Tests whether the retrieved birth year is equal to the specified one in the constructor
     *
     * @throws Exception
     * if the birth year values are not equal
     */
    @Test
    public void testGetBirthDate() throws Exception {
        assertEquals("Birth years should be equal", BIRTH_YEAR, pet.getBirthDate());
    }

    /**
     * Tests whether the freshly set birth year can be retrieved
     *
     * @throws Exception
     * if the new birth year was not properly set
     */
    @Test
    public void testSetBirthDate() throws Exception {
        pet.setBirthDate(NEW_BIRTH_YEAR);
        assertEquals("BirthDates should be equal", NEW_BIRTH_YEAR, pet.getBirthDate());
    }

    @Test
    public void testEquals() {
        Pet pet1 = new Pet("60001", "name1", "breed1", 2018);
        Pet pet2 = new Pet("60002", "name1", "breed2", 2020);
        assertTrue(pet1.equals(pet1));
        assertFalse(pet1.equals(pet2));
        assertFalse(pet2.equals(pet1));
    }

    @Test
    public void testToString() {
        Pet pet1 = new Pet("60001", "name1", "breed1", 2018);
        Pet pet2 = pet1;
        assertTrue(pet1.toString().equals(pet2.toString()));
    }
}