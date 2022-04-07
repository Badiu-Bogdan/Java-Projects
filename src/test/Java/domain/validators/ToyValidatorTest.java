package domain.validators;

import domain.Toy.Toy;
import domain.validators.exceptions.PurchaseValidatorException;
import domain.validators.exceptions.ToyValidatorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyValidatorTest {

    private static final Long ID = new Long(1);
    ToyValidator toyValidator;

    @BeforeEach
    void setUp() { toyValidator = new ToyValidator();
    }

    @AfterEach
    void tearDown() { toyValidator=null;
    }

    @Test
    void validate() {
        Toy toy= new Toy("12345","Gigel",200,"silicon",1D);

        try{
            toyValidator.validate(toy);
            fail();
        }catch(ToyValidatorException e){
            assertEquals(e.getMessage(), "ToyValidatorException The Id must not be null.");
        }

        toy.setId(ID);

        toy.setPrice(-1D);
        try{
            toyValidator.validate(toy);
            fail();
        }catch(ToyValidatorException e){
            assertEquals(e.getMessage(), "ToyValidatorException The price must not be negative.");
        }

        toy.setPrice(1D);
        toy.setWeight(-1);
        try{
            toyValidator.validate(toy);
            fail();
        }catch(ToyValidatorException e){
            assertEquals(e.getMessage(), "ToyValidatorException The weight must not be negative.");
        }

        toy.setWeight(1);
        toy.setName("");
        try{
            toyValidator.validate(toy);
            fail();
        }catch(ToyValidatorException e){
            assertEquals(e.getMessage(), "ToyValidatorException The name must not be empty.");
        }

        toy.setName("Gigel");
        toy.setMaterial("");
        try{
            toyValidator.validate(toy);
            fail();
        }catch(ToyValidatorException e){
            assertEquals(e.getMessage(), "ToyValidatorException Material must not be empty.");
        }

        toy.setMaterial("silicon");
        toy.setSerialNumber("");
        try{
            toyValidator.validate(toy);
            fail();
        }catch(ToyValidatorException e){
            assertEquals(e.getMessage(), "ToyValidatorException SerialNumber must not be empty.");
        }

        toy.setSerialNumber("12a45!");
        try{
           toyValidator.validate(toy);
            fail();
        }catch(ToyValidatorException e){
            assertEquals(e.getMessage(), "ToyValidatorException SerialNumber should contain only digits.");
        }



    }
}