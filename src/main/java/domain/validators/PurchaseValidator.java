package domain.validators;

import domain.Purchase.Purchase;
import domain.validators.exceptions.PurchaseValidatorException;

import java.util.Calendar;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurchaseValidator implements Validator<Purchase> {
    @Override
    public void validate(Purchase entity) throws PurchaseValidatorException {

        // check ID not null
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new PurchaseValidatorException("The Id must not be null.")
        );

        // check clientId non-negative
        Optional.of(entity).filter(purchase -> purchase.getClientId() >= 0).orElseThrow(() ->
            new PurchaseValidatorException("The clientId must not be negative.")
        );

        // check toyId non-negative
        Optional.of(entity).filter(purchase -> purchase.getToyId() >= 0).orElseThrow(() ->
            new PurchaseValidatorException("The toyId must not be negative.")
        );

        // check purchaseYear non-negative
        Optional.of(entity).filter(purchase -> purchase.getPurchaseYear() >= 0).orElseThrow(() ->
            new PurchaseValidatorException("The purchaseYear must not be negative.")
        );

        int year = Calendar.getInstance().get(Calendar.YEAR);
        // check purchaseYear valid
        Optional.of(entity).filter(purchase -> purchase.getPurchaseYear() <= year).orElseThrow(() ->
            new PurchaseValidatorException("The purchaseYear must not be after " + year + ".")
        );

        // check serial-number has only numbers
        Pattern patternNumbers = Pattern.compile("[a-z@!#$%^&*]", Pattern.CASE_INSENSITIVE);
        Matcher matcherNumbers = patternNumbers.matcher(entity.getSerialNumber());
        boolean matchFound = matcherNumbers.find();
        Optional.of(matchFound).filter(m -> !m).orElseThrow(() ->
            new PurchaseValidatorException("SerialNumber should contain only digits.")
        );
    }
}
