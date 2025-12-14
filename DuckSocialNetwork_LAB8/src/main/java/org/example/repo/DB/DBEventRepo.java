package org.example.repo.DB;

import org.example.domain.duck.Duck;
import org.example.domain.duck.DuckType;
import org.example.domain.duck.SwimmingDuck;
import org.example.domain.event.Event;
import org.example.domain.event.RaceEvent;
import org.example.repo.Repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBEventRepo implements Repo<Event> {
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
    public DBEventRepo(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;

        this.connection = DriverManager.getConnection(this.url, this.username, this.password);
    }

    private void addSubclass(Event event) {

        if (event instanceof RaceEvent race) {

            //race_events
            String sqlRace = "INSERT INTO race_events(id) VALUES (?)";

            try (PreparedStatement st = connection.prepareStatement(sqlRace)) {
                st.setLong(1, race.getId());
                st.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            //race_participants
            String sqlPart = "INSERT INTO race_participants(id_event, id_duck) VALUES (?, ?)";

            try (PreparedStatement st = connection.prepareStatement(sqlPart)) {
                for (SwimmingDuck duck : race.getParticipants()) {
                    st.setLong(1, race.getId());
                    st.setLong(2, duck.getId());
                    st.addBatch();
                }
                st.executeBatch();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    @Override
    public void add(Event event) {

        String sql = "INSERT INTO events(name, type) VALUES(?, ?) RETURNING id";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setString(1, event.getName());
            st.setString(2, event.getClass().getSimpleName()); // "RaceEvent"

            var result = st.executeQuery();
            if (result.next()) {
                long id = result.getLong("id");
                event.setId(id);

                addSubclass(event);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Couldn't insert event", e);
        }
    }


    @Override
    public void delete(Long id) {
        String sql_command = "DELETE FROM events WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql_command)){
            statement.setLong(1, id);
            statement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private RaceEvent loadRaceEvent(Long id, String name) {

        String sql = """
        SELECT 
            u.id AS duck_id,
            u.username,
            u.email,
            u.password,
            d.type AS duck_type,
            d.speed,
            d.stamina
        FROM race_participants rp
        JOIN users u ON rp.id_duck = u.id
        JOIN ducks d ON u.id = d.id
        WHERE rp.id_event = ?
        """;

        List<SwimmingDuck> participants = new ArrayList<>();

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            var rs = st.executeQuery();

            while (rs.next()) {
                long duckId = rs.getLong("duck_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                double speed = rs.getDouble("speed");
                double stamina = rs.getDouble("stamina");

                SwimmingDuck duck = new SwimmingDuck(username, email, password,
                        DuckType.SWIMMING, speed, stamina);

                duck.setId(duckId);
                participants.add(duck);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error loading race event participants", ex);
        }

        RaceEvent race = new RaceEvent(name, participants);
        race.setId(id);

        return race;
    }


    @Override
    public List<Event> getAll() {
        List<Event> events = new ArrayList<>();

        String sql = "SELECT * FROM events";

        try (PreparedStatement st = connection.prepareStatement(sql);
             var rs = st.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String type = rs.getString("type");

                if ("RaceEvent".equals(type)) {
                    events.add(loadRaceEvent(id, name));
                }
                else {
                    throw new RuntimeException("Unknown event type: " + type);
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error loading all events", ex);
        }

        return events;
    }


    @Override
    public Event getById(Long id) {

        String sql = "SELECT * FROM events WHERE id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            var rs = st.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type"); // RaceEvent, etc

                if ("RaceEvent".equals(type)) {
                    return loadRaceEvent(id, name);
                } else {
                    throw new RuntimeException("Unknown event type: " + type);
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error loading event by ID", ex);
        }

        return null;
    }

}
