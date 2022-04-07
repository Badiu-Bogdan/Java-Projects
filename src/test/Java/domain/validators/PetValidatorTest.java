package domain.validators;

import domain.Pet.Pet;
import domain.validators.exceptions.AdoptionValidatorException;
import domain.validators.exceptions.PetValidatorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class PetValidatorTest {
    private static final Long ID = new Long(1);

    PetValidator petValidator;

    @BeforeEach
    void setUp() { petValidator = new PetValidator();
    }

    @AfterEach
    void tearDown() { petValidator = null;
    }

    @Test
    void validate() {
        Pet pet = new Pet("12345","Gigel","random",2020);
        try{
            petValidator.validate(pet);
            fail();
        }catch (PetValidatorException e){
            assertEquals(e.getMessage(),"PetValidatorException The Id must not be null.");
        }

        pet.setId(ID);
        pet.setBirthDate(-2020);
        try{

            petValidator.validate(pet);
            fail();
        }catch (PetValidatorException e){
            assertEquals(e.getMessage(),"PetValidatorException The birth date must not be negative.");
        }

        pet.setBirthDate(3000);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        try{
           petValidator.validate(pet);
            fail();
        }catch(PetValidatorException e){
            assertEquals(e.getMessage(), "PetValidatorException The birth year must not be after " + year + ".");
        }
        pet.setBirthDate(2021);
        pet.setName("");
        try{
            petValidator.validate(pet);
            fail();
        }catch(PetValidatorException e){
            assertEquals(e.getMessage(), "PetValidatorException Name must not be empty.");
        }

        pet.setName("Gigel");
        pet.setBreed("");
        try{
            petValidator.validate(pet);
            fail();
        }catch(PetValidatorException e){
            assertEquals(e.getMessage(), "PetValidatorException Breed must not be empty.");
        }

        pet.setBreed("husky");
        pet.setSerialNumber("");
        try{
            petValidator.validate(pet);
            fail();
        }catch(PetValidatorException e){
            assertEquals(e.getMessage(), "PetValidatorException SerialNumber must not be empty.");
        }

        pet.setSerialNumber("12a45!");
        try{
            petValidator.validate(pet);
            fail();
        }catch(PetValidatorException e){
            assertEquals(e.getMessage(), "PetValidatorException SerialNumber should contain only digits.");
        }


    }
}