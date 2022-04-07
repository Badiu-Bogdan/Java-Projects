package service;

import domain.Pet.Pet;
import domain.Toy.Toy;
import domain.validators.exceptions.ValidatorException;
import repository.Repository;
import repository.RepositoryException;
import service.exceptions.PetServiceException;
import service.exceptions.ToyServiceException;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ToyService {
    private final Repository<Long, Toy> repository;

    public ToyService(Repository<Long, Toy> repository) {
        this.repository = repository;
    }

    public void addToy(Toy toy) throws ToyServiceException {
        try{
            repository.save(toy);
        }catch(ValidatorException | IllegalArgumentException e){
            throw new ToyServiceException(e.getMessage());
        }
    }

    public Set<Toy> getAllToys() {
        Iterable<Toy> toys = repository.findAll();
        return StreamSupport.stream(toys.spliterator(), false).collect(Collectors.toSet());
    }

    /**
     * Returns all toys whose name contain the given string.
     *
     * @param s: String to be checked
     * @return filteredToys
     */
    public Set<Toy> filterToysByName(String s) {
        Iterable<Toy> toys = repository.findAll();
        Set<Toy> filteredToys = StreamSupport.stream(toys.spliterator(), false)
                .filter(toy -> toy.getName().contains(s)).collect(Collectors.toSet());

        return filteredToys;
    }

    /**
     * Delete a given toy based on its id
     *
     * @param id: long id of the toy to be deleted
     * @throws ToyServiceException
     *          if toy could not be found or the repository fails
     */

    public void deleteToy(long id) throws ToyServiceException {

        this.repository.findOne(id).orElseThrow(() ->
                new ToyServiceException("could not find toy with the given id.")
        );

        try{
            repository.delete(id);
        }catch(RepositoryException e){
            throw new ToyServiceException(e.getMessage());
        }
    }

    /**
     * Update a given toy based on its id
     *
     * @param id: long id of the toy to be deleted
     *        toy : Toy the new toy
     * @throws PetServiceException
     *          if toy could not be found or the repository fails
     */

    public void updateToy(long id, Toy newToy) throws ToyServiceException{
        this.repository.findOne(id).orElseThrow(() ->
                new ToyServiceException("could not find toy with the given id.")
        );

        try{
            repository.update(newToy);
        }catch(RepositoryException e){
            throw new ToyServiceException(e.getMessage());
        }
    }
}
