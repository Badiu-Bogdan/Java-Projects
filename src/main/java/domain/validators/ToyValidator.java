package domain.validators;

import domain.Toy.Toy;
import domain.validators.exceptions.ToyValidatorException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToyValidator implements Validator<Toy>{
    @Override
    public void validate(Toy entity) throws ToyValidatorException {

        // check ID not null
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new ToyValidatorException("The Id must not be null.")
        );

        // check price non-negative
        Optional.of(entity).filter(toy -> toy.getPrice() >= 0).orElseThrow(() ->
            new ToyValidatorException("The price must not be negative.")
        );

        // check weight non-negative
        Optional.of(entity).filter(toy -> toy.getWeight() >= 0).orElseThrow(() ->
            new ToyValidatorException("The weight must not be negative.")
        );

        // check for non-empty strings
        Pattern pattern = Pattern.compile(".+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(entity.getName());
        Optional.of(!matcher.find()).filter(m -> !m).orElseThrow(() ->
            new ToyValidatorException("Name must not be empty.")
        );
        matcher = pattern.matcher(entity.getMaterial());
        Optional.of(!matcher.find()).filter(m -> !m).orElseThrow(() ->
            new ToyValidatorException("Material must not be empty.")
        );
        matcher = pattern.matcher(entity.getSerialNumber());
        Optional.of(!matcher.find()).filter(m -> !m).orElseThrow(() ->
            new ToyValidatorException("SerialNumber must not be empty.")
        );

        // check serial-number has only numbers
        Pattern patternNumbers = Pattern.compile("[a-z@!#$%^&*]", Pattern.CASE_INSENSITIVE);
        Matcher matcherNumbers = patternNumbers.matcher(entity.getSerialNumber());
        boolean matchFound = matcherNumbers.find();
        Optional.of(matchFound).filter(m -> !m).orElseThrow(() ->
            new ToyValidatorException("SerialNumber should contain only digits.")
        );
    }
}
