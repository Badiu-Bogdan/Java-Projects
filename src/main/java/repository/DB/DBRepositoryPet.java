package repository.DB;

import domain.BaseEntity;
import domain.Pet.Pet;
import domain.validators.Validator;
import repository.DB.exceptions.DBRepositoryPetException;
import repository.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBRepositoryPet<ID, T extends BaseEntity<ID>> implements Repository<ID, T> {

    private Validator<T> validator;

    String DB_URL;
    String USER;
    String PASS;

    public String tableName;

    Connection conn;

    public DBRepositoryPet(Validator<T> validator,String tableName) {
        this.validator = validator;
        this.tableName = tableName;
        try {

            DB_URL = "jdbc:postgresql://localhost:5432/postgres";
            USER = "postgres";
            PASS = "infinite7";
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement createTable = conn.prepareStatement(
                    String.format("CREATE TABLE %s " +
                            "(petId INTEGER not NULL, " +
                            " serialNumber VARCHAR(255), " +
                            " name VARCHAR(255), " +
                            " breed VARCHAR(255), " +
                            " birthDate INTEGER, " +
                            " PRIMARY KEY ( petId ))",tableName));
            createTable.execute();


        }catch (Exception e) {
            throw new DBRepositoryPetException(e.getMessage());
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

        Pet pet = null;
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s WHERE petId = %d",tableName,id));


            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pet = new Pet(rs.getString("serialNumber"),
                        rs.getString("name"), rs.getString("breed"), rs.getInt("birthDate"));
                pet.setId(rs.getLong("petId"));
            }
        }catch (Exception e) {
            throw new DBRepositoryPetException(e.getMessage());
        }
        return (Optional<T>) Optional.ofNullable(pet);

    }

    @Override
    public Iterable<T> findAll() {
        List<Pet> entities = new ArrayList<>();
        Pet pet;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s",tableName));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pet = new Pet(rs.getString("serialNumber"),
                        rs.getString("name"), rs.getString("breed"), rs.getInt("birthDate"));
                pet.setId(rs.getLong("petId"));
                entities.add(pet);
            }
        } catch (Exception e) {
            throw new DBRepositoryPetException(e.getMessage());
        }
        return (Iterable<T>) entities;
    }


    /**
     * Save a pet to the repository
     *
     * @param pet : Pet to be saved to the repository
     * @throws DBRepositoryPetException
     *          if some error regarding the database occurs
     */
    public T saveToDB(Pet pet) throws DBRepositoryPetException {

        try {
            String sql = "insert into " + this.tableName +" values(?,?,?,?,?)";

            PreparedStatement stmt=conn.prepareStatement(sql);
            stmt.setLong(1,pet.getId());
            stmt.setString(2,pet.getSerialNumber());
            stmt.setString(3,pet.getName());
            stmt.setString(4,pet.getBreed());
            stmt.setInt(5,pet.getBirthDate());

            stmt.executeUpdate();


        } catch (Exception e) {
            throw new DBRepositoryPetException(e.getMessage());
        }
        return (T)pet;
    }

    /**
     * Checks if the given entity's id is unique
     *
     * @param entity : T the new entity to be checked
     * @return true if id is unique, false otherwise
     * @throws DBRepositoryPetException
     *          if some error regarding the database occurs
     */
    public boolean checkUniqueId(T entity){

        boolean ct = true;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s where petId = %d",tableName,entity.getId()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ct = false;;
            }
        } catch (Exception e) {
            throw new DBRepositoryPetException(e.getMessage());
        }
        return ct;
    }

    @Override
    public Optional<T> save(T entity) throws DBRepositoryPetException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryPetException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryPetException("id must not be null")
        );

        validator.validate(entity);

        //check if ID already exists
        Optional.of(this.checkUniqueId(entity)).filter(bool -> bool == true).orElseThrow(() ->
                new DBRepositoryPetException("ID taken.")
        );

        saveToDB((Pet) entity);
        return Optional.ofNullable(entity);

    }

    @Override
    public Optional<T> delete(ID id) throws DBRepositoryPetException {
        Optional.ofNullable(id).orElseThrow(() ->
                new DBRepositoryPetException("id must not be null")
        );

        Optional<T> result = findOne(id);
        result.orElseThrow(() ->
                new DBRepositoryPetException("pet with the given id could not be found")
        );
        try {

            PreparedStatement stmt = conn.prepareStatement(
                    String.format("delete from %s where petId=%d",tableName,id));
            stmt.execute();

        } catch (Exception e) {
            throw new DBRepositoryPetException(e.getMessage());
        }
        return result;

    }

    /**
     * Update the given pet in the database
     *
     * @param pet : Pet to update
     * @throws DBRepositoryPetException
     *          if some error regarding the database occurs
     */
    public T updateDB(Pet pet) throws DBRepositoryPetException {

        try {
            String sql = "update " + this.tableName +" set serialNumber = ?, name = ?, breed = ?, " +
                    "BirthDate = ? where petId = ?";

            PreparedStatement stmt=conn.prepareStatement(sql);
            stmt.setString(1,pet.getSerialNumber());
            stmt.setString(2,pet.getName());
            stmt.setString(3,pet.getBreed());
            stmt.setInt(4,pet.getBirthDate());
            stmt.setLong(5,pet.getId());


            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DBRepositoryPetException(e.getMessage());
        }
        return (T)pet;
    }

    @Override
    public Optional<T> update(T entity) throws DBRepositoryPetException {
        Optional.ofNullable(entity).orElseThrow(() ->
                new DBRepositoryPetException("entity must not be null")
        );
        Optional.ofNullable(entity.getId()).orElseThrow(() ->
                new DBRepositoryPetException("id must not be null")
        );

        validator.validate(entity);

        this.findOne(entity.getId()).orElseThrow(() ->
                new DBRepositoryPetException("pet with the given id could not be found")
        );

        updateDB((Pet) entity);
        return Optional.ofNullable(entity);
    }

    /**
     * Drop the created table from the database
     * @throws DBRepositoryPetException
     *          if some error regarding the database occurs
     */
    public void dropTable() throws DBRepositoryPetException {
        try {

            PreparedStatement dropTable = conn.prepareStatement(
                    String.format("DROP TABLE IF EXISTS %s",tableName));
            dropTable.execute();


        } catch (Exception e) {
            throw new DBRepositoryPetException(e.getMessage());
        }
    }


}
