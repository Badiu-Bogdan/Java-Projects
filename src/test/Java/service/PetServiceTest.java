package service;

import domain.Pet.Pet;
import domain.validators.PetValidator;
import domain.validators.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRepository;
import repository.Repository;
import service.exceptions.PetServiceException;

import static org.junit.Assert.*;

public class PetServiceTest {

    private static final Long ID = new Long(1);

    private Validator<Pet> petValidator;
    private Repository<Long, Pet> petRepository;
    private PetService petService;

    @BeforeEach
    void setUp() {
        petValidator = new PetValidator();
        petRepository = new InMemoryRepository<>(petValidator);
        petService = new PetService(petRepository);
    }

    @AfterEach
    void tearDown() {
        petValidator=null;
        petRepository=null;
        petService=null;
    }

    @Test
    void addPet() {
        petRepository = new InMemoryRepository<>(petValidator);
        Pet pet = new Pet("921","name1","breed1",2019);
        pet.setId(ID);
        petService.addPet(pet);
        assertTrue(petService.getAllPets().contains(pet));
        pet.setId(null);
        try{
            petService.addPet(pet);
            fail();
        }catch(PetServiceException e){

        }
    }

    @Test
    void getAllPets() {
    }

    @Test
    void filterPetsByName() {
        petRepository = new InMemoryRepository<>(petValidator);
        Pet pet = new Pet("921","name1","breed1",2019);
        pet.setId(ID);
        petService.addPet(pet);
        assertTrue(petService.filterPetsByName("name").contains(pet));
        assertFalse(petService.filterPetsByName("asd").contains(pet));
    }

    @Test
    void deletePet() {
        petRepository = new InMemoryRepository<>(petValidator);
        Pet pet = new Pet("921","name1","breed1",2019);

        try{
            petService.deletePet(pet.getId());
            fail();
        }catch(NullPointerException e){

        }

        pet.setId(ID);
        petService.addPet(pet);
        petService.deletePet(pet.getId());
        assertFalse(petService.getAllPets().contains(pet));
        assertTrue(petService.getAllPets().isEmpty());

        try{
            petService.deletePet(ID + 1);
            fail();
        }catch(PetServiceException e){

        }
    }

    @Test
    void updatePet() {
        petRepository = new InMemoryRepository<>(petValidator);
        Pet pet = new Pet("921","name1","breed1",2019);

        try{
            petService.updatePet(pet.getId(), null);
            fail();
        }catch(NullPointerException e){

        }

        pet.setId(ID);
        petService.addPet(pet);
        pet.setName("new");
        pet.setBreed("new");
        petService.updatePet(pet.getId(), pet);
        assertTrue(petService.getAllPets().contains(pet));
        assertFalse(petService.getAllPets().isEmpty());

        try{
            petService.updatePet(ID + 1, pet);
            fail();
        }catch(PetServiceException e){

        }
    }
}
