package service;


import domain.Client.Client;
import domain.validators.exceptions.ValidatorException;
import repository.InMemoryRepositoryException;
import repository.Repository;
import repository.RepositoryException;
import service.exceptions.ClientServiceException;
import service.exceptions.PetServiceException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClientService {
    private Repository<Long, Client> repository;

    public ClientService(Repository<Long, Client> repository) {
        this.repository = repository;
    }

    public void addClient(Client client) throws ClientServiceException {
        try {
            repository.save(client);
        }catch(ValidatorException | IllegalArgumentException e){
            throw new ClientServiceException(e.getMessage());
        }
    }

    public Set<Client> getAllClients() {
        Iterable<Client> clients = repository.findAll();
        return StreamSupport.stream(clients.spliterator(), false).collect(Collectors.toSet());
    }

    /**
     * Returns all clients whose name contain the given string.
     *
     * @param s: String to be checked
     * @return filteredClients
     */
    public Set<Client> filterClientsByName(String s) {
        Iterable<Client> clients = repository.findAll();
        Set<Client> filteredClients= new HashSet<>();
        clients.forEach(filteredClients::add);
        filteredClients.removeIf(client -> !client.getName().contains(s));

        return filteredClients;
    }

    /**
     * Delete a given client based on its id
     *
     * @param id: long id of the client to be deleted
     * @throws PetServiceException
     *          if client could not be found or the repository fails
     */

    public void deleteClient(long id) throws ClientServiceException {

        this.repository.findOne(id).orElseThrow(() ->
                new ClientServiceException("could not find client with the given id.")
        );

        try{
            repository.delete(id);
        }catch(RepositoryException e){
            throw new ClientServiceException(e.getMessage());
        }
    }

    /**
     * Update a given client based on its id
     *
     * @param id: long id of the client to be deleted
     *        client : Client the new client
     * @throws ClientServiceException
     *          if client could not be found or the repository fails
     */

    public void updateClient(long id, Client newClient) throws ClientServiceException{
        this.repository.findOne(id).orElseThrow(() ->
                new ClientServiceException("could not find client with the given id.")
        );

        try{
            repository.update(newClient);
        }catch(RepositoryException e){
            throw new ClientServiceException(e.getMessage());
        }
    }
}
