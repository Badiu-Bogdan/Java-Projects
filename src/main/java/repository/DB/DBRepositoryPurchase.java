package repository.DB;

import domain.Purchase.Purchase;
import domain.BaseEntity;
import domain.validators.Validator;
import repository.DB.exceptions.DBRepositoryPurchaseException;
import repository.Repository;

import java.sql.*;
import java.util.*;

public class DBRepositoryPurchase<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;

    String DB_URL;

    String USER;
    String PASS;

    public String tableName;

    Connection conn;

    public DBRepositoryPurchase(Validator<T> validator,String tableName) {
        this.validator = validator;
        this.tableName = tableName;
        try {

            DB_URL = "jdbc:postgresql://localhost:5432/postgres";
            USER = "postgres";
            PASS = "infinite7";
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement createTable = conn.prepareStatement(
                    String.format("CREATE TABLE %s " +
                            "(purchaseId INTEGER not NULL, " +
                            " serialNumber VARCHAR(255), " +
                            " clientId INTEGER, " +
                            " toyId INTEGER, " +
                            " purchaseYear INTEGER, " +
                            " PRIMARY KEY ( purchaseId ))"/* +
                            " FOREIGN KEY (clientId) REFERENCES Client(clientId)" +
                            " FOREIGN KEY (toyId) REFERENCES Toy(toyId))"*/,tableName));
            createTable.execute();


        }catch (Exception e) {
            throw new DBRepositoryPurchaseException(e.getMessage());
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

        Purchase purchase = null;
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s WHERE purchaseId = %d",tableName,id));


            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                purchase = new Purchase(rs.getString("serialNumber"),
                        rs.getLong("clientId"), rs.getLong("toyId"), rs.getInt("purchaseYear"));
                purchase.setId(rs.getLong("purchaseId"));
            }
        }catch (Exception e) {
            throw new DBRepositoryPurchaseException(e.getMessage());
        }
        return (Optional<T>) Optional.ofNullable(purchase);

    }

    @Override
    public Iterable<T> findAll() {
        List<Purchase> entities = new ArrayList<>();
        Purchase purchase;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s",tableName));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                purchase = new Purchase(rs.getString("serialNumber"),
                        rs.getLong("clientId"), rs.getLong("toyId"), rs.getInt("purchaseYear"));
                purchase.setId(rs.getLong("purchaseId"));
                entities.add(purchase);
            }
        } catch (Exception e) {
            throw new DBRepositoryPurchaseException(e.getMessage());
        }
        return (Iterable<T>) entities;
    }


    /**
     * Save an purchase to the repository
     *
     * @param purchase : Purchase to be saved to the repository
     * @throws DBRepositoryPurchaseException
     *          if some error regarding the database occurs
     */
    public T saveToDB(Purchase purchase) throws DBRepositoryPurchaseException {

        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("insert into %s values(%d,%s,%d,%d,%d)",tableName,purchase.getId(),purchase.getSerialNumber(),
                            purchase.getClientId(),purchase.getToyId(),purchase.getPurchaseYear()));
            stmt.execute();


        } catch (Exception e) {
            throw new DBRepositoryPurchaseException(e.getMessage());
        }
        return (T)purchase;
    }

    /**
     * Checks if the given entity's id is unique
     *
     * @param entity : T the new entity to be checked
     * @return true if id is unique, false otherwise
     * @throws DBRepositoryPurchaseException
     *          if some error regarding the database occurs
     */
    public boolean checkUniqueId(T entity){

        boolean ct = true;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s where purchaseId = %d",tableName,entity.getId()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ct = false;;
            }
        } catch (Exception e) {
            throw new DBRepositoryPurchaseException(e.getMessage());
        }
        return ct;
    }

    @Override
    public Optional<T> save(T entity) throws DBRepositoryPurchaseException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryPurchaseException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryPurchaseException("id must not be null")
        );

        validator.validate(entity);

        //check if ID already exists
        Optional.of(this.checkUniqueId(entity)).filter(bool -> bool == true).orElseThrow(() ->
                new DBRepositoryPurchaseException("ID taken.")
        );

        saveToDB((Purchase) entity);
        return Optional.ofNullable(entity);

    }

    @Override
    public Optional<T> delete(ID id) throws DBRepositoryPurchaseException {
        Optional.ofNullable(id).orElseThrow(() ->
                new DBRepositoryPurchaseException("id must not be null")
        );

        Optional<T> result = findOne(id);
        result.orElseThrow(() ->
                new DBRepositoryPurchaseException("purchase with the given id could not be found")
        );
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("delete from %s where purchaseId=%d",tableName,id));
            stmt.execute();

        } catch (Exception e) {
            throw new DBRepositoryPurchaseException(e.getMessage());
        }
        return result;

    }

    /**
     * Update the given purchase in the database
     *
     * @param purchase : Purchase to updated
     * @throws DBRepositoryPurchaseException
     *          if some error regarding the database occurs
     */
    public T updateDB(Purchase purchase) throws DBRepositoryPurchaseException {

        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("update %s set serialNumber = %s, clientId = %d," +
                                    " toyId = %d, purchaseYear = %d where purchaseId = %d",tableName,purchase.getSerialNumber(),
                            purchase.getClientId(),purchase.getToyId(),purchase.getPurchaseYear(),purchase.getId()));
            stmt.execute();

        } catch (Exception e) {
            throw new DBRepositoryPurchaseException(e.getMessage());
        }
        return (T)purchase;
    }

    @Override
    public Optional<T> update(T entity) throws DBRepositoryPurchaseException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryPurchaseException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryPurchaseException("id must not be null")
        );

        validator.validate(entity);

        this.findOne(entity.getId()).orElseThrow(() ->
                new DBRepositoryPurchaseException("purchase with the given id could not be found")
        );

        updateDB((Purchase) entity);
        return Optional.ofNullable(entity);
    }

    /**
     * Drop the created table from the database
     * @throws DBRepositoryPurchaseException
     *          if some error regarding the database occurs
     */
    public void dropTable() throws DBRepositoryPurchaseException {
        try {

            PreparedStatement dropTable = conn.prepareStatement(
                    String.format("DROP TABLE IF EXISTS %s",tableName));
            dropTable.execute();


        } catch (Exception e) {
            throw new DBRepositoryPurchaseException(e.getMessage());
        }
    }


}
