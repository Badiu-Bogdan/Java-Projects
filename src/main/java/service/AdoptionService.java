package service;

import domain.Adoption.Adoption;
import domain.Adoption.AdoptionYearComparator;
import domain.Client.Client;
import domain.Pet.Pet;
import domain.Pet.PetBirthYearComparator;
import domain.validators.exceptions.ValidatorException;
import repository.RepositoryException;
import repository.Repository;
import service.exceptions.AdoptionServiceException;

import java.util.Calendar;
import java.util.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdoptionService {
    private final Repository<Long, Adoption> adoptionRepository;
    private final Repository<Long, Client> clientRepository;
    private final Repository<Long, Pet> petRepository;

    public AdoptionService(Repository<Long, Adoption> adoptionRepository, Repository<Long, Client> clientRepository, Repository<Long, Pet> petRepository) {
        this.adoptionRepository = adoptionRepository;
        this.clientRepository = clientRepository;
        this.petRepository = petRepository;
    }

    /**
     * Adopt a pet given a clientId and a petId
     *
     * @param clientId: Long id of the client
     *        petId: Long id of the pet to be adopted
     * @throws AdoptionServiceException
     *          if the given clientId or petId does not correspond to a Client or Pet from their repositories
     */
    public void adoptPet(Long id, String serialNumber, Long clientId, Long petId) throws AdoptionServiceException {
        clientRepository.findOne(clientId).orElseThrow(() ->
                new AdoptionServiceException("Could not find client with the given clientId.")
        );
        petRepository.findOne(petId).orElseThrow(() ->
                new AdoptionServiceException("Could not find pet with the given petId.")
        );
        // check if pet is available for adoption
        List<Adoption> adoptions = StreamSupport
                .stream(adoptionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // filter adoptions containing the petId
        long cnt = adoptions.stream().filter(adop -> adop.getPetId() == petId).count();
        Optional.of(cnt).filter(c -> c == 0).orElseThrow(() ->
                new AdoptionServiceException("Pet was already adopted.")
        );

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Adoption adoption = new Adoption(serialNumber, clientId, petId, currentYear);
        adoption.setId(id);
        try {
            adoptionRepository.save(adoption);
        }catch(ValidatorException | IllegalArgumentException e){
            throw new AdoptionServiceException(e.getMessage());
        }
    }

    /**
     * Get the average adopted age
     *
     * @return Long with the average adopted age
     * @throws AdoptionServiceException
     *          if the adoptioRepository is empty
     */

    public long getAverageAdoptedAge() throws AdoptionServiceException{

        Iterable<Adoption> iterator = adoptionRepository.findAll();
        List<Adoption> adoptions = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!adoptions.isEmpty()).filter(a -> a).orElseThrow(() ->
                new AdoptionServiceException("in averageAdoptedAge: empty repository of adoptions.")
        );

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // take the adopted pets
        List<Pet> adoptedPets = adoptions.stream()
                .map(p -> petRepository.findOne(p.getPetId()).get())
                .collect(Collectors.toList());

        long sm = adoptedPets.stream()
                .map(p -> (currentYear - p.getBirthDate()))
                .reduce(0, (acc, iter) -> acc += iter);
        return sm / adoptedPets.size();
    }

    /*
    * See what breed was the most wanted in adoptions
    * @param: no param
    * @throws: AdoptionServiceException if there are no adoption registered in the repository
    * @return: String "(MostAdoptedBreed)"
    */
    public String mostAdoptedBreed() throws AdoptionServiceException
    {
        Iterable<Adoption> iterator = adoptionRepository.findAll();
        List<Adoption> adoptions = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!adoptions.isEmpty()).filter(a -> a).orElseThrow(() ->
            new AdoptionServiceException("in mostAdoptedBreed: empty repository of adoptions.")
        );

        // take the adopted pets
        List<Pet> adoptedPets = StreamSupport
                .stream(adoptionRepository.findAll().spliterator(), false)
                .map(p -> petRepository.findOne(p.getPetId()).get())
                .collect(Collectors.toList());

        Map<String, Integer> breed_and_frequency = new HashMap<String, Integer>();
        adoptedPets.forEach(ap -> breed_and_frequency.put(ap.getBreed(), breed_and_frequency.get(ap.getBreed()) == null ? 1 : breed_and_frequency.get(ap.getBreed())+1));

        return breed_and_frequency.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList())
                .stream().limit(1L)
                .collect(Collectors.toList())
                .get(0)
                .getKey();
    }
    /**
    Get the youngest adopted pet.
    @return  Pet the youngest adopted pet | null if the adoptionRepository is empty
    @throws AdoptionServiceException
              if the AdoptionRepository is empty
     */
    public Optional<Pet> getYoungestAdoptedPet() throws AdoptionServiceException
    {
        Iterable<Adoption> iterator = adoptionRepository.findAll();
        List<Adoption> adoptions = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!adoptions.isEmpty()).filter(a -> a).orElseThrow(() ->
                new AdoptionServiceException("in getYoungestAdoptedPet: empty repository of adoptions.")
        );

        List<Pet> pets = adoptions.stream()
                .map(adop -> petRepository.findOne(adop.getPetId()).get())
                .collect(Collectors.toList());

        pets.sort(new PetBirthYearComparator());
        return Optional.of(pets.get(0));
    }

    public Set<Adoption> getAllAdoptions() {
        Iterable<Adoption> adoptions = adoptionRepository.findAll();
        return StreamSupport.stream(adoptions.spliterator(), false).collect(Collectors.toSet());
    }

    /**
     * Get the last n adoptions with a given n
     *
     * @return ArrayList<Adoption> the most recent n adoptions
     */

    public List<Adoption> getLastNAdoptions(int n){
        Iterable<Adoption> iterator = adoptionRepository.findAll();
        List<Adoption> adoptions = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        int finalN = adoptions.size();
        n = Optional.of(n).filter(p -> p < finalN).orElse(adoptions.size());

        adoptions.sort(new AdoptionYearComparator());
        return adoptions.subList(adoptions.size() - n, adoptions.size());
    }

    /*
    Get the client with the biggest number of purchases for a given year.
    @return Client the client of the year | null if the adoptionRepository is empty or if no adoptions were made in that year
    @throws StoreException
            if the adoptionRepository is empty
            if no adoptions were made in that year
     */
    public Client getClientOfTheYear(int year) throws AdoptionServiceException {

        Iterable<Adoption> iterator = adoptionRepository.findAll();
        List<Adoption> adoptions = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!adoptions.isEmpty()).filter(a -> a).orElseThrow(() ->
                new AdoptionServiceException("in getClientOfTheYear: empty repository of adoptions.")
        );

        // take the clients
        List<Client> clients = adoptions
                .stream()
                .filter(ad -> ad.getAdoptionYear() == year)
                .map(c -> clientRepository.findOne(c.getClientId()).get())
                .collect(Collectors.toList());

        Optional.of(!clients.isEmpty()).filter(a -> a).orElseThrow(() ->
                new AdoptionServiceException("in getClientOfTheYear: no adoptions found in the given year.")
        );

        Map<Client, Integer> clientAndFreq = new HashMap<Client, Integer>();
        clients.forEach(cl -> clientAndFreq.put(cl, clientAndFreq.get(cl) == null ? 1 : clientAndFreq.get(cl) + 1));

        return clientAndFreq.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList())
                .stream().limit(1L)
                .collect(Collectors.toList())
                .get(0)
                .getKey();
    }

    /**
     * Get the most recent adoption made
     *
     * @return Adoption the most recent adoption | null if the adoptionRepository is empty
     * @throws AdoptionServiceException
     *          if the AdoptionRepository is empty
     */

    public Adoption getMostRecentAdoption() throws AdoptionServiceException{

        Iterable<Adoption> iterator = adoptionRepository.findAll();
        List<Adoption> adoptions = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        Optional.of(!adoptions.isEmpty()).filter(a -> a).orElseThrow(() ->
                new AdoptionServiceException("in getMostRecentAdoption: empty repository of adoptions.")
        );

        adoptions.sort(new AdoptionYearComparator());
        return adoptions.get(0);
    }

    /**
     * Delete all adoptions for a given client
     *
     * @param id : long id of the client whose adoptions should be deleted
     */

    public void deleteAdoptionsForClient(long id) throws AdoptionServiceException {

        Iterable<Adoption> iterator = adoptionRepository.findAll();
        List<Adoption> adoptions = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        adoptions.stream()
                .filter(client -> client.getClientId() == id)
                .forEach(client -> adoptionRepository.delete(client.getId()));

    }

    /**
     * Delete all adoptions for a given pet
     *
     * @param id : long id of the pet whose adoptions should be deleted
     */

    public void deleteAdoptionsForPet(long id) throws AdoptionServiceException {

        Iterable<Adoption> iterator = adoptionRepository.findAll();
        List<Adoption> adoptions = StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());

        adoptions.stream()
                .filter(adop -> adop.getPetId() == id)
                .forEach(adop -> adoptionRepository.delete(adop.getId()));
    }
}
