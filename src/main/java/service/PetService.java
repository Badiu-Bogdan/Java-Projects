package service;

import domain.Pet.Pet;
import domain.validators.exceptions.ValidatorException;
import repository.Repository;
import repository.RepositoryException;
import service.exceptions.PetServiceException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PetService {
    private Repository<Long, Pet> repository;

    public PetService(Repository<Long, Pet> repository) {
        this.repository = repository;
    }

    public void addPet(Pet pet) throws PetServiceException {
        try{
            repository.save(pet);
        }catch(ValidatorException | IllegalArgumentException e){
            throw new PetServiceException(e.getMessage());
        }
    }

    public Set<Pet> getAllPets() {
        Iterable<Pet> pets = repository.findAll();
        return StreamSupport.stream(pets.spliterator(), false).collect(Collectors.toSet());
    }


    /**
     * Returns all pets whose name contain the given string.
     *
     * @param s: String to be checked
     * @return filteredPets
     */
    public Set<Pet> filterPetsByName(String s) {
        Iterable<Pet> pets = repository.findAll();
        Set<Pet> filteredPets= new HashSet<>();
        pets.forEach(filteredPets::add);
        filteredPets.removeIf(pet -> !pet.getName().contains(s));

        return filteredPets;
    }

    /**
     * Delete a given pet based on its id
     *
     * @param id: long id of the pet to be deleted
     * @throws PetServiceException
     *          if pet could not be found or the repository fails
     */
    public void deletePet(long id) throws PetServiceException{

        this.repository.findOne(id).orElseThrow(() ->
            new PetServiceException("could not find pet with the given id.")
        );

        try{
            repository.delete(id);
        }catch(RepositoryException e){
            throw new PetServiceException(e.getMessage());
        }
    }

    /**
     * Update a given pet based on its id
     *
     * @param id: long id of the pet to be deleted
     *        pet : Pet the new pet
     * @throws PetServiceException
     *          if pet could not be found or the repository fails
     */

    public void updatePet(long id, Pet newPet) throws PetServiceException{
        this.repository.findOne(id).orElseThrow(() ->
                new PetServiceException("could not find pet with the given id.")
        );

        try{
            repository.update(newPet);
        }catch(RepositoryException e){
            throw new PetServiceException(e.getMessage());
        }
    }
}
