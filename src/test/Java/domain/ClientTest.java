package domain;

import domain.Client.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientTest {
    private static final Long ID = new Long(1);
    private static final Long NEW_ID = new Long(2);
    private static final String SERIAL_NUMBER = "sn01";
    private static final String NEW_SERIAL_NUMBER = "sn02";
    private static final String NAME = "PrimulNumeClient";
    private static final String NEW_NAME = "AlDoileaNumeClient";
    private static final String ADDRESS = "Strada Lunga";
    private static final String NEW_ADDRESS = "Strada Scurta";
    private static final int YEAR_OF_REGISTRATION = 2000;
    private static final int NEW_YEAR_OF_REGISTRATION = 2002;

    private Client client;

    @BeforeEach
    public void setUp() throws Exception {
        client = new Client(SERIAL_NUMBER, NAME, ADDRESS, YEAR_OF_REGISTRATION);
        client.setId(ID);
    }

    @AfterEach
    public void tearDown() throws Exception {
        client=null;
    }

    @Test
    public void testGetSerialNumber() throws Exception {
        /*
        Tests whether the retrieved serial number is equal to the specified one in the constructor
         */
        assertEquals("Serial numbers should be equal", SERIAL_NUMBER, client.getSerialNumber());
    }

    @Test
    public void testSetSerialNumber() throws Exception {
        /*
        Tests whether the freshly set serial number can be retrieved
        */
        client.setSerialNumber(NEW_SERIAL_NUMBER);
        assertEquals("Serial numbers should be equal", NEW_SERIAL_NUMBER, client.getSerialNumber());
    }

    @Test
    public void testGetId() throws Exception {
        /*
        Tests whether the retrieved id is equal to the specified one in the constructor
        */
        assertEquals("Ids should be equal", ID, client.getId());
    }

    @Test
    public void testSetId() throws Exception {
        /*
        Tests whether the freshly set id can be retrieved
         */
        client.setId(NEW_ID);
        assertEquals("Ids should be equal", NEW_ID, client.getId());
    }

    @Test
    public void testGetName() throws Exception {
        /*
        Tests whether the retrieved name is equal to the specified one in the constructor
        */
        assertEquals("Names should be equal", NAME, client.getName());
    }

    @Test
    public void testSetName() throws Exception {
        /*
        Tests whether the freshly set name can be retrieved
         */
        client.setName(NEW_NAME);
        assertEquals("Names should be equal", NEW_NAME, client.getName());
    }

    @Test
    public void testGetAddress() throws Exception{
        /*
        Tests whether the received address is equal to the specified one in the constructor
         */
        assertEquals("Address should be equal", ADDRESS, client.getAddress());
    }

    @Test
    public void testSetAddress() throws Exception{
        /*
        Tests whether the new Address can be received
         */
        client.setAddress(NEW_ADDRESS);
        assertEquals("Address should be equal", NEW_ADDRESS, client.getAddress());
    }

    @Test
    public void testGetYearOfRegistration() throws Exception {
        /*
        Tests whether the retrieved yearsOfPractice value is equal to the specified one in the constructor
        */
        assertEquals("Years of practice should be equal", YEAR_OF_REGISTRATION, client.getYearOfRegistration());
    }

    @Test
    public void testSetYearOfRegistration() throws Exception {
        /*
        Tests whether the freshly set years of practice can be retrieved
         */
        client.setYearOfRegistration(NEW_YEAR_OF_REGISTRATION);
        assertEquals("Years of practice should be equal", NEW_YEAR_OF_REGISTRATION, client.getYearOfRegistration());
    }

    @Test
    public void testEquals() {
        Client client1 = new Client("50001","name1","addr1",2019);
        Client client2 = new Client("50002","name2","addr2",2020);
        assertTrue(client1.equals(client1));
        assertFalse(client1.equals(client2));
        assertFalse(client2.equals(client1));
    }

    @Test
    public void testToString() {
        Client client1 = new Client("50001","name1","addr1",2019);
        Client client2 = client1;
        assertTrue(client1.toString().equals(client2.toString()));
    }
}
