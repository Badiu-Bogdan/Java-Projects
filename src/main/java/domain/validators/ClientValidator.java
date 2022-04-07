package domain.validators;

import domain.Client.Client;
import domain.validators.exceptions.ClientValidatorException;

import java.util.Calendar;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientValidator implements Validator<Client> {
    @Override
    public void validate(Client entity) throws ClientValidatorException {

        // check ID not null
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new ClientValidatorException("The Id must not be null.")
        );

        // check yearOfRegistration non-negative
        Optional.of(entity).filter(client -> client.getYearOfRegistration() >= 0).orElseThrow(() ->
            new ClientValidatorException("The yearOfRegistration must not be negative.")
        );

        int year = Calendar.getInstance().get(Calendar.YEAR);
        // check yearOfRegistration valid
        Optional.of(entity).filter(client -> client.getYearOfRegistration() <= year).orElseThrow(() ->
            new ClientValidatorException("The yearOfRegistration must not be after " + year + ".")
        );

        // check for non-empty strings
        Pattern pattern = Pattern.compile(".+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(entity.getName());
        Optional.of(!matcher.find()).filter(m -> !m).orElseThrow(() ->
            new ClientValidatorException("Name must not be empty.")
        );
        matcher = pattern.matcher(entity.getAddress());
        Optional.of(!matcher.find()).filter(m -> !m).orElseThrow(() ->
            new ClientValidatorException("Address must not be empty.")
        );
        matcher = pattern.matcher(entity.getSerialNumber());
        Optional.of(!matcher.find()).filter(m -> !m).orElseThrow(() ->
            new ClientValidatorException("SerialNumber must not be empty.")
        );

        // check serial-number has only numbers
        Pattern patternNumbers = Pattern.compile("[a-z@!#$%^&*]", Pattern.CASE_INSENSITIVE);
        Matcher matcherNumbers = patternNumbers.matcher(entity.getSerialNumber());
        boolean matchFound = matcherNumbers.find();
        Optional.of(matchFound).filter(m -> !m).orElseThrow(() ->
            new ClientValidatorException("SerialNumber should contain only digits.")
        );
    }
}

