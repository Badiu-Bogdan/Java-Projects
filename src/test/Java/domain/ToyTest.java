package domain;

import domain.Pet.Pet;
import domain.Toy.Toy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToyTest {
    private static final Long ID = new Long(1);
    private static final Long NEW_ID = new Long(2);
    private static final String SERIAL_NUMBER = "001";
    private static final String NEW_SERIAL_NUMBER = "003";
    private static final String NAME = "toyName1";
    private static final String NEW_NAME = "toyName2";
    private static final int WEIGHT = 10;
    private static final int NEW_WEIGHT = 15;
    private static final String MATERIAL = "material1";
    private static final String NEW_MATERIAL = "material2";
    private static final double PRICE = 3.99;
    private static final double NEW_PRICE = 2.99;

    private Toy toy;

    @BeforeEach
    public void setUp() throws Exception {
        toy = new Toy(SERIAL_NUMBER, NAME, WEIGHT, MATERIAL, PRICE);
        toy.setId(ID);
    }

    @AfterEach
    public void tearDown() throws Exception {
        toy = null;
    }

    /**
     * Tests whether the retrieved serial number is equal to the specified one in the constructor
     *
     * @throws Exception if serial numbers are not equal
     */
    @Test
    public void testGetSerialNumber() throws Exception {
        assertEquals("Serial numbers should be equal", SERIAL_NUMBER, toy.getSerialNumber());
    }

    /**
     * Tests whether the freshly set serial number can be retrieved
     *
     * @throws Exception if the new serial number was not properly set
     */
    @Test
    public void testSetSerialNumber() throws Exception {
        toy.setSerialNumber(NEW_SERIAL_NUMBER);
        assertEquals("Serial numbers should be equal", NEW_SERIAL_NUMBER, toy.getSerialNumber());
    }

    /**
     * Tests whether the retrieved id is equal to the specified one in the constructor
     *
     * @throws Exception if ids are not equal
     */
    @Test
    public void testGetId() throws Exception {
        assertEquals("Ids should be equal", ID, toy.getId());
    }

    /**
     * Tests whether the freshly set id can be retrieved
     *
     * @throws Exception if the new id was not properly set
     */
    @Test
    public void testSetId() throws Exception {
        toy.setId(NEW_ID);
        assertEquals("Ids should be equal", NEW_ID, toy.getId());
    }

    /**
     * Tests whether the retrieved name is equal to the specified one in the constructor
     *
     * @throws Exception if the names are not equal
     */
    @Test
    public void testGetName() throws Exception {
        assertEquals("Names should be equal", NAME, toy.getName());
    }

    /**
     * Tests whether the freshly set name can be retrieved
     *
     * @throws Exception if the new name was not properly set
     */
    @Test
    public void testSetName() throws Exception {
        toy.setName(NEW_NAME);
        assertEquals("Names should be equal", NEW_NAME, toy.getName());
    }

    /**
     * Tests whether the retrieved weight value is equal to the specified one in the constructor
     *
     * @throws Exception if weights are not equal
     */
    @Test
    public void testGetWeight() throws Exception {
        assertEquals("Weights should be equal", WEIGHT, toy.getWeight());
    }

    /**
     * Tests whether the freshly set weights can be retrieved
     *
     * @throws Exception if the new weight value was not properly set
     */
    @Test
    public void testSetWeight() throws Exception {
        toy.setWeight(NEW_WEIGHT);
        assertEquals("Weights should be equal", NEW_WEIGHT, toy.getWeight());
    }

    /**
     * Tests whether the retrieved material is equal to the specified one in the constructor
     *
     * @throws Exception if materials are not equal
     */
    @Test
    public void testGetMaterial() throws Exception {
        assertEquals("Materials should be equal", MATERIAL, toy.getMaterial());
    }

    /**
     * Tests whether the freshly set material can be retrieved
     *
     * @throws Exception if the new material was not properly set
     */
    @Test
    public void testSetMaterial() throws Exception {
        toy.setMaterial(NEW_MATERIAL);
        assertEquals("Materials should be equal", NEW_MATERIAL, toy.getMaterial());
    }

    /**
     * Tests whether the retrieved price is equal to the specified one in the constructor
     *
     * @throws Exception if prices are not equal
     */
    @Test
    public void testGetPrice() throws Exception {
        Assertions.assertEquals(PRICE, toy.getPrice(), "Prices should be equal");
    }

    /**
     * Tests whether the freshly set price can be retrieved
     *
     * @throws Exception if the new price was not properly set
     */
    @Test
    public void testSetPrice() throws Exception {
        toy.setPrice(NEW_PRICE);
        Assertions.assertEquals(NEW_PRICE, toy.getPrice(), "Prices should be equal");
    }

    @Test
    public void testEquals() {
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        Toy toy2 = new Toy("50002","name2",200,"material2",2.99);
        assertTrue(toy1.equals(toy1));
        assertFalse(toy1.equals(toy2));
        assertFalse(toy2.equals(toy1));
    }

    @Test
    public void testToString() {
        Toy toy1 = new Toy("50001","name1",100,"material1",1.99);
        Toy toy2 = toy1;
        assertTrue(toy1.toString().equals(toy2.toString()));
    }
}
