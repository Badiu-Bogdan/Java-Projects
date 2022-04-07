package domain;

import domain.Adoption.Adoption;
import domain.Purchase.Purchase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PurchaseTest {
    private static final Long ID = 1L;
    private static final Long NEW_ID = 2L;
    private static final String SERIAL_NUMBER = "sn01";
    private static final String NEW_SERIAL_NUMBER = "sn02";
    private static final Long CLIENT_ID = 10L;
    private static final Long NEW_CLIENT_ID = 20L;
    private static final Long TOY_ID = 100L;
    private static final Long NEW_TOY_ID = 200L;
    private static final int PURCHASE_YEAR = 2019;
    private static final int NEW_PURCHASE_YEAR = 2020;

    private Purchase purchase;

    @BeforeEach
    public void setUp() throws Exception {
        purchase = new Purchase(SERIAL_NUMBER, CLIENT_ID, TOY_ID, PURCHASE_YEAR);
        purchase.setId(ID);
    }

    @AfterEach
    public void tearDown() throws Exception {
        purchase=null;
    }

    @Test
    public void testGetSerialNumber() throws Exception {
        /*
        Tests whether the retrieved serial number is equal to the specified one in the constructor
         */
        assertEquals("Serial numbers should be equal", SERIAL_NUMBER, purchase.getSerialNumber());
    }

    @Test
    public void testSetSerialNumber() throws Exception {
        /*
        Tests whether the freshly set serial number can be retrieved
        */
        purchase.setSerialNumber(NEW_SERIAL_NUMBER);
        assertEquals("Serial numbers should be equal", NEW_SERIAL_NUMBER, purchase.getSerialNumber());
    }

    @Test
    public void testGetId() throws Exception {
        /*
        Tests whether the retrieved id is equal to the specified one in the constructor
        */
        assertEquals("Ids should be equal", ID, purchase.getId());
    }

    @Test
    public void testSetId() throws Exception {
        /*
        Tests whether the freshly set id can be retrieved
         */
        purchase.setId(NEW_ID);
        assertEquals("Ids should be equal", NEW_ID, purchase.getId());
    }

    @Test
    public void testGetClientId() throws Exception {
        /*
        Tests whether the retrieved clientId is equal to the specified one in the constructor
        */
        assertEquals("Client Ids should be equal", CLIENT_ID, purchase.getClientId());
    }

    @Test
    public void testSetClientId() throws Exception {
        /*
        Tests whether the freshly set clientId can be retrieved
         */
        purchase.setClientId(NEW_CLIENT_ID);
        assertEquals("Client Ids should be equal", NEW_CLIENT_ID, purchase.getClientId());
    }

    @Test
    public void testGeToyId() throws Exception {
        /*
        Tests whether the retrieved petId is equal to the specified one in the constructor
        */
        assertEquals("Toy Ids should be equal", TOY_ID, purchase.getToyId());
    }

    @Test
    public void testSetPetId() throws Exception {
        /*
        Tests whether the freshly set petId can be retrieved
         */
        purchase.setToyId(NEW_TOY_ID);
        assertEquals("Toy Ids should be equal", NEW_TOY_ID, purchase.getToyId());
    }

    @Test
    public void testGetPurchaseYear() throws Exception {
        /*
        Tests whether the retrieved purchaseYear value is equal to the specified one in the constructor
        */
        assertEquals("Purchase years should be equal", PURCHASE_YEAR, purchase.getPurchaseYear());
    }

    @Test
    public void testSetPurchaseYear() throws Exception {
        /*
        Tests whether the freshly set purchaseYear can be retrieved
         */
        purchase.setPurchaseYear(NEW_PURCHASE_YEAR);
        assertEquals("Purchase years should be equal", NEW_PURCHASE_YEAR, purchase.getPurchaseYear());
    }

    @Test
    public void testEquals() {
        Purchase purchase1 = new Purchase("50001",1L,1L,2018);
        Purchase purchase2 = new Purchase("50002",2L,2L,2020);
        assertTrue(purchase1.equals(purchase1));
        assertFalse(purchase1.equals(purchase2));
        assertFalse(purchase2.equals(purchase1));
    }

    @Test
    public void testToString() {
        Purchase purchase1 = new Purchase("50001",1L,1L,2018);
        Purchase purchase2 = purchase1;
        assertTrue(purchase1.toString().equals(purchase2.toString()));
    }
}
