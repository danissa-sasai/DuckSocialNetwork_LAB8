package org.example.repo.DB;

import org.example.domain.Friendship;
import org.example.domain.Person;
import org.example.domain.duck.*;
import org.example.domain.flock.Flock;
import org.example.domain.flock.FlyingFlock;
import org.example.domain.flock.SwimmingFlock;
import org.example.exceptions.EventException;
import org.example.repo.Repo;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class DBFlockRepo implements Repo<Flock<? extends Duck>> {
    private final String url;
    private final String username;
    private final String password;
    Connection connection;

    /**
     * user repository constructor
     * @param url - the link to the database
     * @param username - the username needed for logging into the database
     * @param password - the password for authentification
     */
    public DBFlockRepo(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;

        this.connection = DriverManager.getConnection(this.url, this.username, this.password);
    }

    @Override
    public void add(Flock<? extends Duck> flock) {
        String sql_command = "INSERT INTO flocks(name, type) VALUES(?,?) RETURNING id;";
        try(PreparedStatement statement = connection.prepareStatement(sql_command)){
            statement.setString(1, flock.getName());
            statement.setString(2, flock.getType().name());

            var result_id = statement.executeQuery();
            if(result_id.next()){
                Long id = result_id.getLong("id");
                flock.setId(id);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        //adds the flock's members
        sql_command = "INSERT INTO flocks_members(id_flock, id_duck) VALUES(?,?);";
        try(PreparedStatement statement = connection.prepareStatement(sql_command)){
            for (var duck : flock.getMembers()){
                statement.setLong(1, flock.getId());
                statement.setLong(2, duck.getId());

                statement.addBatch(); //ca sa execut mai multe instructiuni deodata
            }

            statement.executeBatch();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql_command = "DELETE FROM flocks WHERE id = ?;";
        try(PreparedStatement statement = connection.prepareStatement(sql_command)){
            statement.setLong(1, id);

            statement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private Flock<? extends Duck> loadFlock(Long id, String name, String type) {

        String sql = """
        SELECT 
            u.id AS duck_id,
            u.username,
            u.email,
            u.password,
            d.type AS duck_type,
            d.speed,
            d.stamina
        FROM flocks_members fm
        JOIN users u ON fm.id_duck = u.id
        JOIN ducks d ON u.id = d.id
        WHERE fm.id_flock = ?
    """;

        List<Duck> members = new ArrayList<>();

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {

                    long duckId = rs.getLong("duck_id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password");

                    DuckType duckType = DuckType.valueOf(rs.getString("duck_type"));
                    double speed = rs.getDouble("speed");
                    double stamina = rs.getDouble("stamina");

                    Duck duck = switch (duckType) {
                        case SWIMMING -> new SwimmingDuck(username, email, password, duckType, speed, stamina);
                        case FLYING -> new FlyingDuck(username, email, password, duckType, speed, stamina);
                        case FLYING_AND_SWIMMING -> new FlyingSwimmingDuck(username, email, password, duckType, speed, stamina);
                    };

                    duck.setId(duckId);
                    duck.setFlock(null); //va fi setat ulterior
                    members.add(duck);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // reconstrucție flock
        DuckType flockType = DuckType.valueOf(type);

        if(flockType == DuckType.FLYING){
            Flock<? extends Duck> flockObj = new FlyingFlock(name, flockType,
                    members.stream().map(d -> (FlyingDuck) d).toList());
            flockObj.setId(id);
            return flockObj;
        }

        else if (flockType == DuckType.SWIMMING){
            Flock<? extends Duck> flockObj = new SwimmingFlock(name, flockType,
                    members.stream().map(d -> (SwimmingDuck) d).toList());
            flockObj.setId(id);
            return flockObj;
        }
        else
            throw new RuntimeException("Unrecognized Duck type");

    }

    @Override
    public List<Flock<? extends Duck>> getAll() {
        List<Flock<? extends Duck>> flocks = new ArrayList<>();

        String sql = "SELECT * FROM flocks";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                long id = result.getLong("id");
                String name = result.getString("name");
                String type = result.getString("type");

                Flock<? extends Duck> flock = loadFlock(id, name, type);
                flocks.add(flock);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error while loading all flocks!", e);
        }

        return flocks;
    }


    @Override
    public Flock getById(Long id) {
        String sql = "SELECT * FROM flocks WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String name = result.getString("name");
                String type = result.getString("type");
                return loadFlock(id,name,type);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while searching for the friendship!", e);
        }
        return null;
    }

    /**
     * calculates the average speed and the average stamina of the flock's ducks
     * @return - the average speed and the average stamina
     * @throws - EventException if the flock is not found in the repo
     */
    public AbstractMap.SimpleEntry<Double, Double> getAveragePerformance(Long id) {
        Flock flock = getById(id);
        if (flock == null) {
            throw new EventException("Flock not found!");
        }

        return flock.getAveragePerformance();
    }
}
