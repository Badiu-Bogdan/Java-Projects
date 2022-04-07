package service;

import domain.Toy.Toy;
import domain.validators.ToyValidator;
import domain.validators.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRepository;
import repository.Repository;
import service.exceptions.ToyServiceException;

import static org.junit.Assert.*;

public class ToyServiceTest {

    private static final Long ID = new Long(1);

    private Validator<Toy> toyValidator;
    private Repository<Long, Toy> toyRepository;
    private ToyService toyService;

    @BeforeEach
    void setUp() {
        toyValidator = new ToyValidator();
        toyRepository = new InMemoryRepository<>(toyValidator);
        toyService = new ToyService(toyRepository);
    }

    @AfterEach
    void tearDown() {
        toyValidator=null;
        toyRepository=null;
        toyService=null;
    }

    @Test
    void addToy() {
        toyRepository = new InMemoryRepository<>(toyValidator);
        Toy toy = new Toy("50001","name1",100,"material1",1.99);
        toy.setId(ID);
        toyService.addToy(toy);
        assertTrue(toyService.getAllToys().contains(toy));
        toy.setId(null);
        try{
            toyService.addToy(toy);
            fail();
        }catch(ToyServiceException e){

        }
    }

    @Test
    void getAllToys() {
    }

    @Test
    void filterToysByName() {
        toyRepository = new InMemoryRepository<>(toyValidator);
        Toy toy = new Toy("50001","name1",100,"material1",1.99);
        toy.setId(ID);
        toyService.addToy(toy);
        assertTrue(toyService.filterToysByName("name").contains(toy));
        assertFalse(toyService.filterToysByName("asd").contains(toy));
    }

    @Test
    void deleteToy() {
        toyRepository = new InMemoryRepository<>(toyValidator);
        Toy toy = new Toy("50001","name1",100,"material1",1.99);

        try{
            toyService.deleteToy(toy.getId());
            fail();
        }catch(NullPointerException e){

        }

        toy.setId(ID);
        toyService.addToy(toy);
        toyService.deleteToy(toy.getId());
        assertFalse(toyService.getAllToys().contains(toy));
        assertTrue(toyService.getAllToys().isEmpty());

        try{
            toyService.deleteToy(ID + 1);
            fail();
        }catch(ToyServiceException e){

        }
    }

    @Test
    void updateToy() {
        toyRepository = new InMemoryRepository<>(toyValidator);
        Toy toy = new Toy("50001","name1",100,"material1",1.99);

        try{
            toyService.updateToy(toy.getId(), null);
            fail();
        }catch(NullPointerException e){

        }

        toy.setId(ID);
        toyService.addToy(toy);
        toy.setName("new");
        toy.setMaterial("new");
        toyService.updateToy(toy.getId(), toy);
        assertTrue(toyService.getAllToys().contains(toy));
        assertFalse(toyService.getAllToys().isEmpty());

        try{
            toyService.updateToy(ID + 1, toy);
            fail();
        }catch(ToyServiceException e){

        }
    }
}
