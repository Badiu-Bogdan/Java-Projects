package domain.validators;

import domain.Adoption.Adoption;
import domain.validators.exceptions.AdoptionValidatorException;

import java.util.Calendar;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdoptionValidator implements Validator<Adoption>{
    @Override
    public void validate(Adoption entity) throws AdoptionValidatorException {

        // check ID not null
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
            new AdoptionValidatorException("The Id must not be null.")
        );

        // check clientId non-negative
        Optional.of(entity).filter(adoption -> adoption.getClientId() >= 0).orElseThrow(() ->
            new AdoptionValidatorException("The clientId must not be negative.")
        );

        // check adoptionId non-negative
        Optional.of(entity).filter(adoption -> adoption.getPetId() >= 0).orElseThrow(() ->
            new AdoptionValidatorException("The petId must not be negative.")
        );

        // check adoptionYear non-negative
        Optional.of(entity).filter(adoption -> adoption.getAdoptionYear() >= 0).orElseThrow(() ->
            new AdoptionValidatorException("The adoptionYear must not be negative.")
        );

        int year = Calendar.getInstance().get(Calendar.YEAR);
        // check adoptionYear valid
        Optional.of(entity).filter(adoption -> adoption.getAdoptionYear() <= year).orElseThrow(() ->
            new AdoptionValidatorException("The adoptionYear must not be after " + year + ".")
        );

        // check serial-number has only numbers
        Pattern patternNumbers = Pattern.compile("[a-z@!#$%^&*]", Pattern.CASE_INSENSITIVE);
        Matcher matcherNumbers = patternNumbers.matcher(entity.getSerialNumber());
        boolean matchFound = matcherNumbers.find();
        Optional.of(matchFound).filter(m -> !m).orElseThrow(() ->
            new AdoptionValidatorException("SerialNumber should contain only digits.")
        );
    }
}
