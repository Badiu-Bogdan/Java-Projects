package repository.DB;

import domain.BaseEntity;
import domain.Toy.Toy;
import domain.validators.Validator;
import repository.DB.exceptions.DBRepositoryToyException;
import repository.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBRepositoryToy<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;

    String DB_URL;

    String USER;
    String PASS;

    public String tableName;

    Connection conn;

    public DBRepositoryToy(Validator<T> validator,String tableName) {
        this.validator = validator;
        this.tableName = tableName;
        try {

            DB_URL = "jdbc:postgresql://localhost:5432/postgres";
            USER = "postgres";
            PASS = "infinite7";
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement createTable = conn.prepareStatement(
                    String.format("CREATE TABLE %s " +
                            "(toyId INTEGER not NULL, " +
                            " serialNumber VARCHAR(255), " +
                            " name VARCHAR(255), " +
                            " weight INTEGER, " +
                            " material VARCHAR(255), " +
                            " price INTEGER, " +
                            " PRIMARY KEY ( toyId ))",tableName));
            createTable.execute();


        }catch (Exception e) {
            throw new DBRepositoryToyException(e.getMessage());
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

        Toy toy = null;
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s WHERE toyId = %d",tableName,id));


            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                toy = new Toy(rs.getString("serialNumber"),
                        rs.getString("name"), rs.getInt("weight"),
                        rs.getString("material"),rs.getDouble("price"));
                toy.setId(rs.getLong("toyId"));
            }
        }catch (Exception e) {
            throw new DBRepositoryToyException(e.getMessage());
        }
        return (Optional<T>) Optional.ofNullable(toy);

    }

    @Override
    public Iterable<T> findAll() {
        List<Toy> entities = new ArrayList<>();
        Toy toy;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s",tableName));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                toy = new Toy(rs.getString("serialNumber"),
                        rs.getString("name"), rs.getInt("weight"),
                        rs.getString("material"),rs.getDouble("price"));
                toy.setId(rs.getLong("toyId"));
                entities.add(toy);
            }
        } catch (Exception e) {
            throw new DBRepositoryToyException(e.getMessage());
        }
        return (Iterable<T>) entities;
    }


    /**
     * Save an toy to the repository
     *
     * @param toy : Toy to be saved to the repository
     * @throws DBRepositoryToyException
     *          if some error regarding the database occurs
     */
    public T saveToDB(Toy toy) throws DBRepositoryToyException {

        try {


            String sql = "insert into " + this.tableName +" values(?,?,?,?,?,?)";

            PreparedStatement stmt=conn.prepareStatement(sql);
            stmt.setLong(1,toy.getId());
            stmt.setString(2,toy.getSerialNumber());
            stmt.setString(3,toy.getName());
            stmt.setInt(4,toy.getWeight());
            stmt.setString(5,toy.getMaterial());
            stmt.setDouble(6,toy.getPrice());

            stmt.executeUpdate();


        } catch (Exception e) {
            throw new DBRepositoryToyException(e.getMessage());
        }
        return (T)toy;
    }

    /**
     * Checks if the given entity's id is unique
     *
     * @param entity : T the new entity to be checked
     * @return true if id is unique, false otherwise
     * @throws DBRepositoryToyException
     *          if some error regarding the database occurs
     */
    public boolean checkUniqueId(T entity){

        boolean ct = true;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s where toyId = %d",tableName,entity.getId()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ct = false;;
            }
        } catch (Exception e) {
            throw new DBRepositoryToyException(e.getMessage());
        }
        return ct;
    }

    @Override
    public Optional<T> save(T entity) throws DBRepositoryToyException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryToyException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryToyException("id must not be null")
        );

        validator.validate(entity);

        //check if ID already exists
        Optional.of(this.checkUniqueId(entity)).filter(bool -> bool == true).orElseThrow(() ->
                new DBRepositoryToyException("ID taken.")
        );

        saveToDB((Toy) entity);
        return Optional.ofNullable(entity);

    }

    @Override
    public Optional<T> delete(ID id) throws DBRepositoryToyException {
        Optional.ofNullable(id).orElseThrow(() ->
                new DBRepositoryToyException("id must not be null")
        );

        Optional<T> result = findOne(id);
        result.orElseThrow(() ->
                new DBRepositoryToyException("toy with the given id could not be found")
        );
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("delete from %s where toyId=%d",tableName,id));
            stmt.execute();

        } catch (Exception e) {
            throw new DBRepositoryToyException(e.getMessage());
        }
        return result;

    }

    /**
     * Update the given toy in the database
     *
     * @param toy : Toy to updated
     * @throws DBRepositoryToyException
     *          if some error regarding the database occurs
     */
    public T updateDB(Toy toy) throws DBRepositoryToyException {

        try {

            String sql = "update " + this.tableName +" set serialNumber = ?, name = ?, weight = ?, " +
                    "material = ?, price = ? where toyId = ?";

            PreparedStatement stmt=conn.prepareStatement(sql);
            stmt.setString(1,toy.getSerialNumber());
            stmt.setString(2,toy.getName());
            stmt.setInt(3,toy.getWeight());
            stmt.setString(4,toy.getMaterial());
            stmt.setDouble(5,toy.getPrice());
            stmt.setLong(6,toy.getId());


            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DBRepositoryToyException(e.getMessage());
        }
        return (T)toy;
    }

    @Override
    public Optional<T> update(T entity) throws DBRepositoryToyException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryToyException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryToyException("id must not be null")
        );

        validator.validate(entity);

        this.findOne(entity.getId()).orElseThrow(() ->
                new DBRepositoryToyException("toy with the given id could not be found")
        );

        updateDB((Toy) entity);
        return Optional.ofNullable(entity);
    }

    /**
     * Drop the created table from the database
     * @throws DBRepositoryToyException
     *          if some error regarding the database occurs
     */
    public void dropTable() throws DBRepositoryToyException {
        try {

            PreparedStatement dropTable = conn.prepareStatement(
                    String.format("DROP TABLE IF EXISTS %s",tableName));
            dropTable.execute();


        } catch (Exception e) {
            throw new DBRepositoryToyException(e.getMessage());
        }
    }


}
