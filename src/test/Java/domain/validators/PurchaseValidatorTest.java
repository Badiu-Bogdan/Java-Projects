package domain.validators;

import domain.Purchase.Purchase;
import domain.validators.exceptions.AdoptionValidatorException;
import domain.validators.exceptions.PurchaseValidatorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseValidatorTest {
    private static final Long ID = new Long(1);

    PurchaseValidator purchaseValidator;

    @BeforeEach
    void setUp() { purchaseValidator= new PurchaseValidator();
    }

    @AfterEach
    void tearDown() { purchaseValidator= null;
    }

    @Test
    void validate() {
        Purchase purchase=new Purchase("12345",1L,1L,2020);
        try{
            purchaseValidator.validate(purchase);
            fail();
        }catch(PurchaseValidatorException e){
            assertEquals(e.getMessage(), "PurchaseValidatorException The Id must not be null.");
        }

        purchase.setId(ID);

        purchase.setClientId(-1L);
        try{
            purchaseValidator.validate(purchase);
            fail();
        }catch(PurchaseValidatorException e){
            assertEquals(e.getMessage(), "PurchaseValidatorException The clientId must not be negative.");
        }

        purchase.setClientId(1L);
        purchase.setToyId(-1L);
        try{
            purchaseValidator.validate(purchase);
            fail();
        }catch(PurchaseValidatorException e){
            assertEquals(e.getMessage(), "PurchaseValidatorException The toyId must not be negative.");
        }

        purchase.setToyId(1L);
        purchase.setPurchaseYear(-2000);
        try{
            purchaseValidator.validate(purchase);
            fail();
        }catch(PurchaseValidatorException e){
            assertEquals(e.getMessage(), "PurchaseValidatorException The purchaseYear must not be negative.");
        }

        purchase.setPurchaseYear(3000);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        try{
            purchaseValidator.validate(purchase);
            fail();
        }catch(PurchaseValidatorException e){
            assertEquals(e.getMessage(), "PurchaseValidatorException The purchaseYear must not be after " + year + ".");
        }

        purchase.setPurchaseYear(2021);
        purchase.setSerialNumber("12a45!");
        try{
            purchaseValidator.validate(purchase);
            fail();
        }catch(PurchaseValidatorException e){
            assertEquals(e.getMessage(), "PurchaseValidatorException SerialNumber should contain only digits.");
        }
    }
}