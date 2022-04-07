package service;

import domain.Adoption.Adoption;
import domain.Client.Client;
import domain.Pet.Pet;
import domain.validators.AdoptionValidator;
import domain.validators.ClientValidator;
import domain.validators.PetValidator;
import domain.validators.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRepository;
import repository.Repository;
import service.exceptions.AdoptionServiceException;

import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AdoptionServiceTest {

    private static final Long ID = new Long(1);

    private Validator<Client> clientValidator;
    private Repository<Long, Client> clientRepository;
    private ClientService clientService;
    private Validator<Pet> petValidator;
    private Repository<Long, Pet> petRepository;
    private PetService petService;
    private Validator<Adoption> adoptionValidator;
    private Repository<Long, Adoption> adoptionRepository;
    private AdoptionService adoptionService;

    @BeforeEach
    void setUp() {
        clientValidator = new ClientValidator();
        clientRepository = new InMemoryRepository<>(clientValidator);
        clientService = new ClientService(clientRepository);
        petValidator = new PetValidator();
        petRepository = new InMemoryRepository<>(petValidator);
        petService = new PetService(petRepository);
        adoptionValidator = new AdoptionValidator();
        adoptionRepository = new InMemoryRepository<>(adoptionValidator);
        adoptionService = new AdoptionService(adoptionRepository, clientRepository, petRepository);
    }

    @AfterEach
    void tearDown() {
        clientValidator=null;
        clientRepository=null;
        clientService=null;
        petValidator=null;
        petRepository=null;
        petService=null;
        adoptionValidator=null;
        adoptionRepository=null;
        adoptionService=null;
    }

    @Test
    void adoptPet() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        Pet pet = new Pet("60001","name1","breed1",2019);
        pet.setId(ID+1);
        try {
            adoptionService.adoptPet(ID, "99999", client.getId(), pet.getId());
            fail();
        }catch(AdoptionServiceException e){

        }
        clientRepository.save(client);
        try {
            adoptionService.adoptPet(ID, "99999", client.getId(), pet.getId());
            fail();
        }catch(AdoptionServiceException e){

        }
        petRepository.save(pet);
        adoptionService.adoptPet(ID, "99999", client.getId(), pet.getId());

        try{
            adoptionService.adoptPet(ID, "19999", client.getId(), pet.getId());
            fail();
        }catch(AdoptionServiceException e){}
    }

    @Test
    void getAverageAdoptedAge() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        int year1 = 2010;
        Pet pet1 = new Pet("60001","name1","breed1",year1);
        pet1.setId(ID+1);
        petRepository.save(pet1);
        int year2 = 2016;
        Pet pet2 = new Pet("60002","name2","breed2",year2);
        pet2.setId(ID+2);
        petRepository.save(pet2);
        int year3 = 2019;
        Pet pet3 = new Pet("60003","name3","breed3",year3);
        pet3.setId(ID+3);
        petRepository.save(pet3);

        try{
            adoptionService.getAverageAdoptedAge();
            fail();
        }catch(AdoptionServiceException e){}

        adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
        adoptionService.adoptPet(ID+1, "12346", client.getId(), pet2.getId());
        adoptionService.adoptPet(ID+2, "12347", client.getId(), pet3.getId());
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int expectedAverage = ((currentYear - year1) + (currentYear - year2) + (currentYear - year3)) / 3;
        assertEquals(expectedAverage, adoptionService.getAverageAdoptedAge());
    }

    @Test
    void mostAdoptedBreed() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        int year1 = 2010;
        Pet pet1 = new Pet("60001","name1","birman",year1);
        pet1.setId(ID+1);
        petRepository.save(pet1);
        int year2 = 2016;
        Pet pet2 = new Pet("60002","name2","bulldog",year2);
        pet2.setId(ID+2);
        petRepository.save(pet2);
        int year3 = 2019;
        Pet pet3 = new Pet("60003","name3","birman",year3);
        pet3.setId(ID+3);
        petRepository.save(pet3);

        try{
            adoptionService.mostAdoptedBreed();
            fail();
        }catch(AdoptionServiceException e){}

        adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
        adoptionService.adoptPet(ID+1, "12346", client.getId(), pet2.getId());
        adoptionService.adoptPet(ID+2, "12347", client.getId(), pet3.getId());
        assertEquals("birman", adoptionService.mostAdoptedBreed());
    }

    @Test
    void getYoungestAdoptedPet() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        int year1 = 2010;
        Pet pet1 = new Pet("60001","name1","birman",year1);
        pet1.setId(ID+1);
        petRepository.save(pet1);
        int year2 = 2016;
        Pet pet2 = new Pet("60002","name2","bulldog",year2);
        pet2.setId(ID+2);
        petRepository.save(pet2);
        int year3 = 2019;
        Pet pet3 = new Pet("60003","name3","birman",year3);
        pet3.setId(ID+3);
        petRepository.save(pet3);

        try{
            adoptionService.getYoungestAdoptedPet();
            fail();
        }catch(AdoptionServiceException e){}

        adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
        adoptionService.adoptPet(ID+1, "12346", client.getId(), pet2.getId());
        adoptionService.adoptPet(ID+2, "12347", client.getId(), pet3.getId());
        assertEquals(Optional.of(pet3), adoptionService.getYoungestAdoptedPet());
    }

    @Test
    void getAllAdoptions() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        int year1 = 2010;
        Pet pet1 = new Pet("60001","name1","birman",year1);
        pet1.setId(ID+1);
        petRepository.save(pet1);
        int year2 = 2016;
        Pet pet2 = new Pet("60002","name2","bulldog",year2);
        pet2.setId(ID+2);
        petRepository.save(pet2);
        int year3 = 2019;
        Pet pet3 = new Pet("60003","name3","birman",year3);
        pet3.setId(ID+3);
        petRepository.save(pet3);

        adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
        adoptionService.adoptPet(ID+1, "12346", client.getId(), pet2.getId());
        adoptionService.adoptPet(ID+2, "12347", client.getId(), pet3.getId());
        assertEquals(3, adoptionService.getAllAdoptions().size());
    }

    @Test
    void getLastNAdoptions() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        int year1 = 2010;
        Pet pet1 = new Pet("60001","name1","birman",year1);
        pet1.setId(ID+1);
        petRepository.save(pet1);
        int year2 = 2016;
        Pet pet2 = new Pet("60002","name2","bulldog",year2);
        pet2.setId(ID+2);
        petRepository.save(pet2);
        int year3 = 2019;
        Pet pet3 = new Pet("60003","name3","birman",year3);
        pet3.setId(ID+3);
        petRepository.save(pet3);

        assertEquals(0, adoptionService.getLastNAdoptions(100).size());

        adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
        assertEquals(1, adoptionService.getLastNAdoptions(100).size());

        adoptionService.adoptPet(ID+1, "12346", client.getId(), pet2.getId());
        assertEquals(2, adoptionService.getLastNAdoptions(100).size());
        assertEquals(1, adoptionService.getLastNAdoptions(1).size());

        adoptionService.adoptPet(ID+2, "12347", client.getId(), pet3.getId());
        assertEquals(3, adoptionService.getLastNAdoptions(100).size());
        assertEquals(2, adoptionService.getLastNAdoptions(2).size());
        assertEquals(1, adoptionService.getLastNAdoptions(1).size());

    }

    @Test
    void getClientOfTheYear() {
        Client client1 = new Client("50001","name1","addr1",2019);
        client1.setId(ID);
        clientRepository.save(client1);
        Pet pet1 = new Pet("60001","name1","birman",2016);
        pet1.setId(ID+1);
        petRepository.save(pet1);
        Pet pet2 = new Pet("60002","name2","bulldog",2017);
        pet2.setId(ID+2);
        petRepository.save(pet2);
        Pet pet3 = new Pet("60003","name3","birman",2020);
        pet3.setId(ID+3);
        petRepository.save(pet3);

        Client client2 = new Client("50002","name2","addr2",2016);
        client2.setId(ID+1);
        clientRepository.save(client2);
        Pet pet4 = new Pet("60004","name4","birman",2021);
        pet4.setId(ID+4);
        petRepository.save(pet4);

        try{
            adoptionService.getClientOfTheYear(2021);
            fail();
        }catch(AdoptionServiceException e){}

        adoptionService.adoptPet(ID, "12345", client1.getId(), pet1.getId());
        adoptionService.adoptPet(ID+1, "12346", client1.getId(), pet2.getId());
        adoptionService.adoptPet(ID+2, "12347", client1.getId(), pet3.getId());
        adoptionService.adoptPet(ID+3, "12348", client2.getId(), pet4.getId());

        assertEquals(client2, adoptionService.getClientOfTheYear(2021));

        try{
            adoptionService.getClientOfTheYear(2005);
            fail();
        }catch(AdoptionServiceException e){}
    }

    @Test
    void getMostRecentAdoption() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        Pet pet1 = new Pet("60001","name1","birman",2015);
        pet1.setId(ID+1);
        petRepository.save(pet1);
        Pet pet2 = new Pet("60002","name2","bulldog",2017);
        pet2.setId(ID+2);
        petRepository.save(pet2);

        try{
            adoptionService.getMostRecentAdoption();
            fail();
        }catch(AdoptionServiceException e){}

        adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
        Adoption adop1 = adoptionService.getMostRecentAdoption();
        assertEquals(adop1.getId(), ID);
        assertEquals(adop1.getSerialNumber(), "12345");
        assertEquals(adop1.getClientId(), client.getId());
        assertEquals(adop1.getPetId(), pet1.getId());
    }

    @Test
    void deleteAdoptionsForClient() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        Pet pet1 = new Pet("60001","name1","birman",2015);
        pet1.setId(ID+1);
        petRepository.save(pet1);

        adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
        assertEquals(1, adoptionService.getAllAdoptions().size());

        try{
            adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
            fail();
        }catch(AdoptionServiceException e){}

        adoptionService.deleteAdoptionsForClient(client.getId());
        assertEquals(0, adoptionService.getAllAdoptions().size());
    }

    @Test
    void deleteAdoptionsForPet() {
        Client client = new Client("50001","name1","addr1",2019);
        client.setId(ID);
        clientRepository.save(client);
        Pet pet1 = new Pet("60001","name1","birman",2015);
        pet1.setId(ID+1);
        petRepository.save(pet1);

        adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
        assertEquals(1, adoptionService.getAllAdoptions().size());

        try{
            adoptionService.adoptPet(ID, "12345", client.getId(), pet1.getId());
            fail();
        }catch(AdoptionServiceException e){}

        adoptionService.deleteAdoptionsForPet(pet1.getId());
        assertEquals(0, adoptionService.getAllAdoptions().size());
    }
}
