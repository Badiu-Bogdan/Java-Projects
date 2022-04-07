package repository.DB;

import domain.BaseEntity;
import domain.Client.Client;
import domain.validators.Validator;
import repository.DB.exceptions.DBRepositoryClientException;
import repository.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBRepositoryClient<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;

    String DB_URL; //database url

    String USER;
    String PASS;

    String tableName;

    Connection conn;

    public DBRepositoryClient(Validator<T> validator,String tableName) {
        this.validator = validator;
        this.tableName = tableName;
        try {

            DB_URL = "jdbc:postgresql://localhost:5432/postgres";
            USER = "postgres";
            PASS = "infinite7";
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement createTable = conn.prepareStatement(
                    String.format("CREATE TABLE %s" +
                            "(clientId INTEGER not NULL, " +
                            " serialNumber VARCHAR(255), " +
                            " name VARCHAR(255), " +
                            " address VARCHAR(255), " +
                            " yearOfRegistration INTEGER, " +
                            " PRIMARY KEY ( clientId ))",this.tableName));
            createTable.execute();


        }catch (Exception e) {
            throw new DBRepositoryClientException(e.getMessage());
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    @Override
    public Optional<T> findOne(ID id) {
        Client client = null;
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s WHERE clientId = %d",tableName,id));


            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                client = new Client(rs.getString("serialNumber"),
                        rs.getString("name"),  rs.getString("address"), rs.getInt("yearOfRegistration"));
                client.setId(rs.getLong("clientId"));
            }
        }catch (Exception e) {
            throw new DBRepositoryClientException(e.getMessage());
        }
        return (Optional<T>) Optional.ofNullable(client);

    }

    @Override
    public Iterable<T> findAll() {
        List<Client> entities = new ArrayList<>();
        Client client;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s",tableName));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                client = new Client(rs.getString("serialNumber"),
                        rs.getString("name"),  rs.getString("address"), rs.getInt("yearOfRegistration"));
                client.setId(rs.getLong("clientId"));
                entities.add(client);
            }
        } catch (Exception e) {
            throw new DBRepositoryClientException(e.getMessage());
        }
        return (Iterable<T>) entities;
    }


    /**
     * Save a client to the repository
     *
     * @param client : Client to be saved to the repository
     * @throws DBRepositoryClientException
     *          if some error regarding the database occurs
     */
    public T saveToDB(Client client) throws DBRepositoryClientException {

        try {
            String sql = "insert into " + this.tableName +" values(?,?,?,?,?)";

            PreparedStatement stmt=conn.prepareStatement(sql);
            stmt.setLong(1,client.getId());
            stmt.setString(2,client.getSerialNumber());
            stmt.setString(3,client.getName());
            stmt.setString(4,client.getAddress());
            stmt.setInt(5,client.getYearOfRegistration());

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DBRepositoryClientException(e.getMessage());
        }
        return (T)client;
    }

    /**
     * Checks if the given entity's id is unique
     *
     * @param entity : T the new entity to be checked
     * @return true if id is unique, false otherwise
     * @throws DBRepositoryClientException
     *          if some error regarding the database occurs
     */
    public boolean checkUniqueId(T entity){

        boolean ct = true;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s where clientId = %d",tableName,entity.getId()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ct = false;;
            }
        } catch (Exception e) {
            throw new DBRepositoryClientException(e.getMessage());
        }
        return ct;
    }

    @Override
    public Optional<T> save(T entity) throws DBRepositoryClientException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryClientException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryClientException("id must not be null")
        );

        validator.validate(entity);

        //check if ID already exists
        Optional.of(this.checkUniqueId(entity)).filter(bool -> bool == true).orElseThrow(() ->
                new DBRepositoryClientException("ID taken.")
        );

        saveToDB((Client) entity);
        return Optional.ofNullable(entity);

    }

    @Override
    public Optional<T> delete(ID id) throws DBRepositoryClientException {
        Optional.ofNullable(id).orElseThrow(() ->
                new DBRepositoryClientException("id must not be null")
        );

        Optional<T> result = findOne(id);
        result.orElseThrow(() ->
                new DBRepositoryClientException("client with the given id could not be found")
        );
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("delete from %s where clientId=%d",tableName,id));
            stmt.execute();

        } catch (Exception e) {
            throw new DBRepositoryClientException(e.getMessage());
        }
        return result;

    }

    /**
     * Update the given client in the database
     *
     * @param client : Client to updated
     * @throws DBRepositoryClientException
     *          if some error regarding the database occurs
     */
    public T updateDB(Client client) throws DBRepositoryClientException {

        try {
            String sql = "update " + this.tableName +" set serialNumber = ?, name = ?, address = ?, " +
                    "yearOfRegistration = ? where clientId = ?";

            PreparedStatement stmt=conn.prepareStatement(sql);
            stmt.setString(1,client.getSerialNumber());
            stmt.setString(2,client.getName());
            stmt.setString(3,client.getAddress());
            stmt.setInt(4,client.getYearOfRegistration());
            stmt.setLong(5,client.getId());


            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DBRepositoryClientException(e.getMessage());
        }
        return (T)client;
    }

    @Override
    public Optional<T> update(T entity) throws DBRepositoryClientException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryClientException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryClientException("id must not be null")
        );

        validator.validate(entity);

        this.findOne(entity.getId()).orElseThrow(() ->
                new DBRepositoryClientException("client with the given id could not be found")
        );

        updateDB((Client) entity);
        return Optional.ofNullable(entity);
    }

    /**
     * Drop the created table from the database
     * @throws DBRepositoryClientException
     *          if some error regarding the database occurs
     */
    public void dropTable() throws DBRepositoryClientException {
        try {

            PreparedStatement dropTable = conn.prepareStatement(
                    String.format("DROP TABLE IF EXISTS %s",tableName));
            dropTable.execute();

        } catch (Exception e) {
            throw new DBRepositoryClientException(e.getMessage());
        }
    }


}
