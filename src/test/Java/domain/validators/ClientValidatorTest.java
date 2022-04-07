package domain.validators;

import domain.Client.Client;
import domain.validators.exceptions.ClientValidatorException;
import domain.validators.exceptions.PetValidatorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class ClientValidatorTest {

    private static final Long ID = new Long(1);

    ClientValidator clientValidator;

    @BeforeEach
    void setUp() { clientValidator = new ClientValidator();
    }

    @AfterEach
    void tearDown() { clientValidator=null;
    }

    @Test
    void validate() {
        Client client= new Client("12345","Gigel","Mihai Eminescu",2020);
        try{
            clientValidator.validate(client);
            fail();
        }catch (ClientValidatorException e){
            assertEquals(e.getMessage(),"ClientValidatorException The Id must not be null.");
        }

        client.setId(ID);
        client.setYearOfRegistration(-2000);
        try{

            clientValidator.validate(client);
            fail();
        }catch (ClientValidatorException e){
            assertEquals(e.getMessage(),"ClientValidatorException The yearOfRegistration must not be negative.");
        }

        client.setYearOfRegistration(3000);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        try{

            clientValidator.validate(client);
            fail();
        }catch (ClientValidatorException e){
            assertEquals(e.getMessage(),"ClientValidatorException The yearOfRegistration must not be after " + year + ".");
        }

        client.setYearOfRegistration(2021);
        client.setName("");
        try{
            clientValidator.validate(client);
            fail();
        }catch (ClientValidatorException e){
            assertEquals(e.getMessage(),"ClientValidatorException Name must not be empty.");
        }

        client.setName("Gigel");
        client.setAddress("");
        try{
            clientValidator.validate(client);
            fail();
        }catch (ClientValidatorException e){
            assertEquals(e.getMessage(),"ClientValidatorException Address must not be empty.");
        }

        client.setAddress("Mihai Eminescu");
        client.setSerialNumber("");
        try{
            clientValidator.validate(client);
            fail();
        }catch (ClientValidatorException e){
            assertEquals(e.getMessage(),"ClientValidatorException SerialNumber must not be empty.");
        }

        client.setSerialNumber("12a45!");
        try{
            clientValidator.validate(client);
            fail();
        }catch (ClientValidatorException e){
            assertEquals(e.getMessage(),"ClientValidatorException SerialNumber should contain only digits.");
        }


    }
}