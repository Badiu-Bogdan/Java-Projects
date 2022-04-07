package domain.validators;

import domain.Pet.Pet;
import domain.validators.exceptions.PetValidatorException;

import java.util.Calendar;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PetValidator implements Validator<Pet> {
    @Override
    public void validate(Pet entity) throws PetValidatorException {

        // check ID not null
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new PetValidatorException("The Id must not be null.")
        );

        // check birth year non-negative
        Optional.of(entity).filter(pet -> pet.getBirthDate() >= 0).orElseThrow(() ->
            new PetValidatorException("The birth date must not be negative.")
        );

        int year = Calendar.getInstance().get(Calendar.YEAR);
        // check birthDate valid
        Optional.of(entity).filter(pet -> pet.getBirthDate() <= year).orElseThrow(() ->
            new PetValidatorException("The birth year must not be after " + year + ".")
        );

        // check for non-empty strings
        Pattern pattern = Pattern.compile(".+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(entity.getName());
        Optional.of(!matcher.find()).filter(m -> !m).orElseThrow(() ->
            new PetValidatorException("Name must not be empty.")
        );
        matcher = pattern.matcher(entity.getBreed());
        Optional.of(!matcher.find()).filter(m -> !m).orElseThrow(() ->
            new PetValidatorException("Breed must not be empty.")
        );
        matcher = pattern.matcher(entity.getSerialNumber());
        Optional.of(!matcher.find()).filter(m -> !m).orElseThrow(() ->
            new PetValidatorException("SerialNumber must not be empty.")
        );

        // check serial-number has only numbers
        Pattern patternNumbers = Pattern.compile("[a-z@!#$%^&*]", Pattern.CASE_INSENSITIVE);
        Matcher matcherNumbers = patternNumbers.matcher(entity.getSerialNumber());
        boolean matchFound = matcherNumbers.find();
        Optional.of(matchFound).filter(m -> !m).orElseThrow(() ->
            new PetValidatorException("SerialNumber should contain only digits.")
        );
    }
}
