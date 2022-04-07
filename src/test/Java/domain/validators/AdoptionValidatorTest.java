package domain.validators;

import domain.Adoption.Adoption;
import domain.validators.exceptions.AdoptionValidatorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class AdoptionValidatorTest {

    private static final Long ID = new Long(1);

    AdoptionValidator adoptionValidator;

    @BeforeEach
    void setUp() {
        adoptionValidator = new AdoptionValidator();
    }

    @AfterEach
    void tearDown() {
        adoptionValidator=null;
    }

    @Test
    void validate() {
        Adoption adoption = new Adoption("12345",1L,1L,2020);
        try{
            adoptionValidator.validate(adoption);
            fail();
        }catch(AdoptionValidatorException e){
            assertEquals(e.getMessage(), "AdoptionValidatorException The Id must not be null.");
        }

        adoption.setId(ID);

        adoption.setClientId(-1L);
        try{
            adoptionValidator.validate(adoption);
            fail();
        }catch(AdoptionValidatorException e){
            assertEquals(e.getMessage(), "AdoptionValidatorException The clientId must not be negative.");
        }

        adoption.setClientId(1L);
        adoption.setPetId(-1L);
        try{
            adoptionValidator.validate(adoption);
            fail();
        }catch(AdoptionValidatorException e){
            assertEquals(e.getMessage(), "AdoptionValidatorException The petId must not be negative.");
        }

        adoption.setPetId(1L);
        adoption.setAdoptionYear(-2000);
        try{
            adoptionValidator.validate(adoption);
            fail();
        }catch(AdoptionValidatorException e){
            assertEquals(e.getMessage(), "AdoptionValidatorException The adoptionYear must not be negative.");
        }

        adoption.setAdoptionYear(3000);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        try{
            adoptionValidator.validate(adoption);
            fail();
        }catch(AdoptionValidatorException e){
            assertEquals(e.getMessage(), "AdoptionValidatorException The adoptionYear must not be after " + year + ".");
        }

        adoption.setAdoptionYear(2021);
        adoption.setSerialNumber("12a45!");
        try{
            adoptionValidator.validate(adoption);
            fail();
        }catch(AdoptionValidatorException e){
            assertEquals(e.getMessage(), "AdoptionValidatorException SerialNumber should contain only digits.");
        }
    }
}