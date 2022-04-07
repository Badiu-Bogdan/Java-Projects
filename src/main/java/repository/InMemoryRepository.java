package repository;

import domain.BaseEntity;
import domain.validators.Validator;
import domain.validators.exceptions.ValidatorException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InMemoryRepository<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Map<ID, T> entities;
    private Validator<T> validator;

    public InMemoryRepository(Validator<T> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public Optional<T> findOne(ID id) throws InMemoryRepositoryException {
        Optional.ofNullable(id).orElseThrow(() ->
            new InMemoryRepositoryException("id must not be null")
        );

        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<T> findAll() {
        Set<T> allEntities = entities.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toSet());
        return allEntities;
    }

    @Override
    public Optional<T> save(T entity) throws IllegalArgumentException, ValidatorException {

        Optional<T> result = Optional.empty();

        Optional.ofNullable(entity).orElseThrow(() ->
                new IllegalArgumentException("entity must not be null")
        );

        validator.validate(entity); // throws directly

        // check id taken
        result = Optional.ofNullable(this.entities.get(entity.getId()));

        result.orElse(entities.put(entity.getId(), entity));

        return result; // saved correctly
    }

    @Override
    public Optional<T> delete(ID id) throws IllegalArgumentException {

        Optional.ofNullable(id).orElseThrow(() ->
                new IllegalArgumentException("id must not be null")
        );

        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<T> update(T entity) throws IllegalArgumentException, ValidatorException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new IllegalArgumentException("entity must not be null")
        );

        validator.validate(entity);

        T result = entities.computeIfPresent(entity.getId(), (k, v) -> entity);
        // result is null if update failed

        Optional<T> opt = Optional.ofNullable(result).filter(e -> e == null);

        // fail to update? return entity
        return opt;
    }
}
