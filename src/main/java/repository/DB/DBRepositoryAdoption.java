package repository.DB;

import domain.Adoption.Adoption;
import domain.BaseEntity;
import domain.validators.Validator;
import repository.DB.exceptions.DBRepositoryAdoptionException;
import repository.Repository;

import java.sql.*;
import java.util.*;

public class DBRepositoryAdoption<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;

    String DB_URL;

    String USER;
    String PASS;

    public String tableName;

    Connection conn;

    public DBRepositoryAdoption(Validator<T> validator,String tableName) {
        this.validator = validator;
        this.tableName = tableName;
        try {

            DB_URL = "jdbc:postgresql://localhost:5432/postgres";
            USER = "postgres";
            PASS = "infinite7";
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement createTable = conn.prepareStatement(
                    String.format("CREATE TABLE %s " +
                            "(adoptionId INTEGER not NULL, " +
                            " serialNumber VARCHAR(255), " +
                            " clientId INTEGER, " +
                            " petId INTEGER, " +
                            " adoptionYear INTEGER, " +
                            " PRIMARY KEY ( adoptionId ))"/* +
                            " FOREIGN KEY (clientId) REFERENCES Client(clientId)" +
                            " FOREIGN KEY (petId) REFERENCES Pet(petId))"*/,tableName));
            createTable.execute();


        }catch (Exception e) {
            throw new DBRepositoryAdoptionException(e.getMessage());
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

        Adoption adoption = null;
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s WHERE adoptionId = %d",tableName,id));


            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                adoption = new Adoption(rs.getString("serialNumber"),
                        rs.getLong("clientId"), rs.getLong("petId"), rs.getInt("adoptionYear"));
                adoption.setId(rs.getLong("adoptionId"));
            }
        }catch (Exception e) {
            throw new DBRepositoryAdoptionException(e.getMessage());
        }
        return (Optional<T>) Optional.ofNullable(adoption);

    }

    @Override
    public Iterable<T> findAll() {
        List<Adoption> entities = new ArrayList<>();
        Adoption adoption;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s",tableName));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                adoption = new Adoption(rs.getString("serialNumber"),
                        rs.getLong("clientId"), rs.getLong("petId"), rs.getInt("adoptionYear"));
                adoption.setId(rs.getLong("adoptionId"));
                entities.add(adoption);
            }
        } catch (Exception e) {
            throw new DBRepositoryAdoptionException(e.getMessage());
        }
        return (Iterable<T>) entities;
    }


    /**
     * Save an adoption to the repository
     *
     * @param adoption : Adoption to be saved to the repository
     * @throws DBRepositoryAdoptionException
     *          if some error regarding the database occurs
     */
    public T saveToDB(Adoption adoption) throws DBRepositoryAdoptionException {

        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("insert into %s values(%d,%s,%d,%d,%d)",tableName,adoption.getId(),adoption.getSerialNumber(),
                            adoption.getClientId(),adoption.getPetId(),adoption.getAdoptionYear()));
            stmt.execute();


        } catch (Exception e) {
            throw new DBRepositoryAdoptionException(e.getMessage());
        }
        return (T)adoption;
    }

    /**
     * Checks if the given entity's id is unique
     *
     * @param entity : T the new entity to be checked
     * @return true if id is unique, false otherwise
     * @throws DBRepositoryAdoptionException
     *          if some error regarding the database occurs
     */
    public boolean checkUniqueId(T entity){

        boolean ct = true;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s where adoptionId = %d",tableName,entity.getId()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ct = false;;
            }
        } catch (Exception e) {
            throw new DBRepositoryAdoptionException(e.getMessage());
        }
        return ct;
    }

    @Override
    public Optional<T> save(T entity) throws DBRepositoryAdoptionException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryAdoptionException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryAdoptionException("id must not be null")
        );

        validator.validate(entity);

        //check if ID already exists
        Optional.of(this.checkUniqueId(entity)).filter(bool -> bool == true).orElseThrow(() ->
                new DBRepositoryAdoptionException("ID taken.")
        );

        saveToDB((Adoption) entity);
        return Optional.ofNullable(entity);

    }

    @Override
    public Optional<T> delete(ID id) throws DBRepositoryAdoptionException {
        Optional.ofNullable(id).orElseThrow(() ->
                new DBRepositoryAdoptionException("id must not be null")
        );

        Optional<T> result = findOne(id);
        result.orElseThrow(() ->
                new DBRepositoryAdoptionException("adoption with the given id could not be found")
        );
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("delete from %s where adoptionId=%d",tableName,id));
            stmt.execute();

        } catch (Exception e) {
            throw new DBRepositoryAdoptionException(e.getMessage());
        }
        return result;

    }

    /**
     * Update the given adoption in the database
     *
     * @param adoption : Adoption to updated
     * @throws DBRepositoryAdoptionException
     *          if some error regarding the database occurs
     */
    public T updateDB(Adoption adoption) throws DBRepositoryAdoptionException {

        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("update %s set serialNumber = %s, clientId = %d," +
                            " petId = %d, adoptionYear = %d where adoptionId = %d",tableName,adoption.getSerialNumber(),
                            adoption.getClientId(),adoption.getPetId(),adoption.getAdoptionYear(),adoption.getId()));
            stmt.execute();

        } catch (Exception e) {
            throw new DBRepositoryAdoptionException(e.getMessage());
        }
        return (T)adoption;
    }

    @Override
    public Optional<T> update(T entity) throws DBRepositoryAdoptionException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryAdoptionException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryAdoptionException("id must not be null")
        );

        validator.validate(entity);

        this.findOne(entity.getId()).orElseThrow(() ->
                new DBRepositoryAdoptionException("adoption with the given id could not be found")
        );

        updateDB((Adoption) entity);
        return Optional.ofNullable(entity);
    }

    /**
     * Drop the created table from the database
     * @throws DBRepositoryAdoptionException
     *          if some error regarding the database occurs
     */
    public void dropTable() throws DBRepositoryAdoptionException {
        try {

            PreparedStatement dropTable = conn.prepareStatement(
                    String.format("DROP TABLE IF EXISTS %s",tableName));
            dropTable.execute();


        } catch (Exception e) {
            throw new DBRepositoryAdoptionException(e.getMessage());
        }
    }


}
