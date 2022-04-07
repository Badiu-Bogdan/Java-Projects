package domain;

import domain.Adoption.Adoption;
import domain.Pet.Pet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdoptionTest {
    private static final Long ID = 1L;
    private static final Long NEW_ID = 2L;
    private static final String SERIAL_NUMBER = "sn01";
    private static final String NEW_SERIAL_NUMBER = "sn02";
    private static final Long CLIENT_ID = 10L;
    private static final Long NEW_CLIENT_ID = 20L;
    private static final Long PET_ID = 100L;
    private static final Long NEW_PET_ID = 200L;
    private static final int ADOPTION_YEAR = 2019;
    private static final int NEW_ADOPTION_YEAR = 2020;

    private Adoption adoption;

    @BeforeEach
    public void setUp() throws Exception {
        adoption = new Adoption(SERIAL_NUMBER, CLIENT_ID, PET_ID, ADOPTION_YEAR);
        adoption.setId(ID);
    }

    @AfterEach
    public void tearDown() throws Exception {
        adoption=null;
    }

    @Test
    public void testGetSerialNumber() throws Exception {
        /*
        Tests whether the retrieved serial number is equal to the specified one in the constructor
         */
        assertEquals("Serial numbers should be equal", SERIAL_NUMBER, adoption.getSerialNumber());
    }

    @Test
    public void testSetSerialNumber() throws Exception {
        /*
        Tests whether the freshly set serial number can be retrieved
        */
        adoption.setSerialNumber(NEW_SERIAL_NUMBER);
        assertEquals("Serial numbers should be equal", NEW_SERIAL_NUMBER, adoption.getSerialNumber());
    }

    @Test
    public void testGetId() throws Exception {
        /*
        Tests whether the retrieved id is equal to the specified one in the constructor
        */
        assertEquals("Ids should be equal", ID, adoption.getId());
    }

    @Test
    public void testSetId() throws Exception {
        /*
        Tests whether the freshly set id can be retrieved
         */
        adoption.setId(NEW_ID);
        assertEquals("Ids should be equal", NEW_ID, adoption.getId());
    }

    @Test
    public void testGetClientId() throws Exception {
        /*
        Tests whether the retrieved clientId is equal to the specified one in the constructor
        */
        assertEquals("Client Ids should be equal", CLIENT_ID, adoption.getClientId());
    }

    @Test
    public void testSetClientId() throws Exception {
        /*
        Tests whether the freshly set clientId can be retrieved
         */
        adoption.setClientId(NEW_CLIENT_ID);
        assertEquals("Client Ids should be equal", NEW_CLIENT_ID, adoption.getClientId());
    }

    @Test
    public void testGetPetId() throws Exception {
        /*
        Tests whether the retrieved petId is equal to the specified one in the constructor
        */
        assertEquals("Pet Ids should be equal", PET_ID, adoption.getPetId());
    }

    @Test
    public void testSetPetId() throws Exception {
        /*
        Tests whether the freshly set petId can be retrieved
         */
        adoption.setPetId(NEW_PET_ID);
        assertEquals("Pet Ids should be equal", NEW_PET_ID, adoption.getPetId());
    }

    @Test
    public void testGetAdoptionYear() throws Exception {
        /*
        Tests whether the retrieved adoptionYear value is equal to the specified one in the constructor
        */
        assertEquals("Adoption years should be equal", ADOPTION_YEAR, adoption.getAdoptionYear());
    }

    @Test
    public void testSetAdoptionYear() throws Exception {
        /*
        Tests whether the freshly set adoptionYear can be retrieved
         */
        adoption.setAdoptionYear(NEW_ADOPTION_YEAR);
        assertEquals("Adoption years should be equal", NEW_ADOPTION_YEAR, adoption.getAdoptionYear());
    }

    @Test
    public void testEquals() {
        Adoption adoption1 = new Adoption("50001",1L,1L,2018);
        Adoption adoption2 = new Adoption("50002",2L,2L,2020);
        assertTrue(adoption1.equals(adoption1));
        assertFalse(adoption1.equals(adoption2));
        assertFalse(adoption2.equals(adoption1));
    }

    @Test
    public void testToString() {
        Adoption adoption1 = new Adoption("50001",1L,1L,2018);
        Adoption adoption2 = adoption1;
        assertTrue(adoption1.toString().equals(adoption2.toString()));
    }
}
