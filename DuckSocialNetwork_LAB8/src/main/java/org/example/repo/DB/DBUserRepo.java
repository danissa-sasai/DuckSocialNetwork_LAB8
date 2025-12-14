package org.example.repo.DB;

import org.example.domain.Person;
import org.example.domain.User;
import org.example.domain.duck.*;
import org.example.dto.DuckFilterDTO;
import org.example.dto.PersonFilterDTO;
import org.example.exceptions.UserException;
import org.example.repo.DuckPagingRepo;
import org.example.repo.PagingRepo;
import org.example.repo.PersonPagingRepo;
import org.example.utils.Pair;
import org.example.utils.PasswordHasher;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * user repository that manages persons and ducks and keeps their data in a database
 */
public class DBUserRepo implements DuckPagingRepo, PersonPagingRepo {
    private final String url;
    private final String username;
    private final String password;
    Connection connection;
    private final PasswordHasher passwordHasher = new PasswordHasher();

    /**
     * user repository constructor
     * @param url - the link to the database
     * @param username - the username needed for logging into the database
     * @param password - the password for authentification
     */
    public DBUserRepo(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;

        this.connection = DriverManager.getConnection(this.url, this.username, this.password);
    }

    /**
     * adds a user into the database - the user can be a duck or a person
     * @param id - the id generated for the user
     * @param user - a user that has all the parameters set except for the id
     * @throws SQLException - if there is an issue while executing the sql commands
     */
    private void addSubclass(Long id, User user) throws SQLException {
        if (user instanceof Person person) {
            String sql_command = "INSERT INTO persons(id,lastname,firstname,birthdate,occupation,empathylevel) " +
                    "VALUES (?,?,?,?,?,?)";
            var statement = connection.prepareStatement(sql_command);

            statement.setLong(1, id);
            statement.setString(2, person.getLastName());
            statement.setString(3, person.getFirstName());
            statement.setDate(4, java.sql.Date.valueOf(person.getBirthDate()) );
            statement.setString(5, person.getOccupation());
            statement.setInt(6,person.getEmpathyLevel());

            statement.executeUpdate();
        }
        else if (user instanceof Duck duck) {
            String sql_command = "INSERT INTO ducks(id,type,speed,stamina) VALUES (?,?,?,?);";
            var statement = connection.prepareStatement(sql_command);
            statement.setLong(1, id);
            statement.setString(2, duck.getType().name()); //.name() returneaza un string ce are arata fix cum e scris in enum
            statement.setDouble(3, duck.getSpeed());
            statement.setDouble(4, duck.getStamina());

            statement.executeUpdate();
        }
    }

    /**
     * adds a user to the repo database
     * @param user - a duck user | a person user
     */
    @Override
    public void add(User user) {


        String sql_command = "INSERT INTO users(username,email,password,type) VALUES(?,?,?,?) RETURNING id;";
        try (PreparedStatement statement = connection.prepareStatement(sql_command);) {
            String hashedPassword = passwordHasher.hashPassword(user.getPassword());

            //first - setting the fields available in the user table
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashedPassword);
            statement.setString(4, user instanceof Duck ? "DUCK" : "PERSON");

            var result = statement.executeQuery(); // doar pt comenzi cu ce returneaza cv
            if(result.next()) { //.next pt a citi randurile -> return true daca citeste | false altfel
                Long id = result.getLong("id");
                user.setId(id);
                addSubclass(id, user);
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * deletes a user from the repositories database
     * @param id - the id of the entity that is deleted
     */
    @Override
    public void delete(Long id) {
        String sql_command = "DELETE FROM users WHERE id=?";
        try(var statement = connection.prepareStatement(sql_command)){
            statement.setLong(1, id);
            statement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * loads a person user from the repo database
     * @param id - the id needed for the search
     * @param userUsername - the user's username
     * @param userEmail - the user's email
     * @param userPassword - the user's password
     * @return - the person user found with the specified id
     * @throws SQLException - if there are issues while loading the data
     */
    private Person loadPerson(long id, String userUsername, String userEmail, String userPassword) throws SQLException {
        String sql = "SELECT * FROM persons WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next())
                throw new IllegalArgumentException("There is no column in Persons for id = " + id);

            String lastName = rs.getString("lastname");
            String firstName = rs.getString("firstname");
            LocalDate birthdate = rs.getDate("birthdate").toLocalDate();
            String occupation = rs.getString("occupation");
            int empathylevel = rs.getInt("empathylevel");

            Person p = new Person(userUsername,userEmail,userPassword, lastName,firstName, birthdate, occupation, empathylevel);
            p.setId(id);
            return p;
        }
    }

    /**
     * loads a duck user from the repo database
     * @param id - the id needed for the search
     * @param userUsername - the user's username
     * @param userEmail - the user's email
     * @param userPassword - the user's password
     * @return - the person user found with the specified id
     * @throws SQLException - if there are issues while loading the data
     */
    private Duck loadDuck(long id, String userUsername, String userEmail, String userPassword) throws SQLException {
        String sql = "SELECT * FROM ducks WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next())
                throw new IllegalArgumentException("There is no column for id=" + id);

            String type = rs.getString("type");
            double speed = rs.getDouble("speed");
            double stamina = rs.getDouble("stamina");

            Duck d;
            DuckType duckType = DuckType.valueOf(type);
            switch (type) {
                case "SWIMMING":
                    d = new SwimmingDuck(userUsername,userEmail,userPassword,duckType,speed,stamina);
                    break;
                case "FLYING":
                    d = new FlyingDuck(userUsername,userEmail,userPassword,duckType,speed,stamina);
                    break;
                case "FLYING_AND_SWIMMING":
                    d = new FlyingSwimmingDuck(userUsername,userEmail,userPassword,duckType,speed,stamina);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown duck type: " + type);
            }
            d.setId(id);
            return d;
        }
    }

    /**
     * gets a user from the user repository
     * @param rs - the result set from a sql command that gets a user from the Users table in the database
     * @return - a person user | a duck user
     * @throws SQLException - if there are issues while loading the data
     */
    private User getUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String type = rs.getString("type"); // DUCK / PERSON

        if (type == null)
            throw new IllegalStateException("The user with id =" + id + " doesn t have a type");

        if (type.equalsIgnoreCase("PERSON")) {
            return loadPerson(id, username, email, password);
        } else if (type.equalsIgnoreCase("DUCK")) {
            return loadDuck(id, username, email, password);
        }

        throw new IllegalArgumentException("Unknows type for the user with id =" + id + ": " + type);
    }

    /**
     * gets a user from the user repository
     * @param id - the id of the user
     * @return - the user with the specified id
     */
    @Override
    public User getById(Long id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return getUser(result);
            }
        } catch (SQLException e) {
            throw new UserException("Error while searching for the user");
        }
        return null;
    }

    /**
     * gets all the users from the user repository
     * @return - all the users currently in the database
     */
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (var statement = connection.prepareStatement(sql);
             var result = statement.executeQuery()) {
            while (result.next()) {
                users.add(getUser(result));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while loading the users!", e);
        }
        return users;
    }

    public Pair<User, Boolean> getUserByEmailPassword(String email, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE email = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (var result = statement.executeQuery()) {
                if (!result.next()) {
                    return new Pair<>(null, false);
                }

                User user = getUser(result);
                String hashedPassword = passwordHasher.hashPassword(password);

                boolean correctPassword = user.getPassword().equals(hashedPassword);

                return new Pair<>(user, correctPassword);
            }
        }
    }

    /// -------------------------------------------PT PAGEABLE DUCKS----------------------------------------------------------------

    private Pair<String, List<Object>> toSql(DuckFilterDTO filter) {
        if (filter == null) {
            return new Pair<>("", Collections.emptyList());
        }
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if( filter.getType() != null) {
            conditions.add("d.type = ?");
            params.add(filter.getType());
        }

        String sql = String.join(" and ", conditions);
        return new Pair<>(sql, params);
    }

    private int duckCount(DuckFilterDTO filter) throws SQLException {
        String sql_command = "SELECT count(*) AS count FROM users u JOIN ducks d ON u.id = d.id";

        Pair<String, List<Object>> sqlFilter = toSql(filter);
        if (!sqlFilter.getFirst().isEmpty()) {
            sql_command += " where " + sqlFilter.getFirst();
        }
        try (PreparedStatement statement = connection.prepareStatement(sql_command)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getSecond()) {
                statement.setObject(++paramIndex, param , Types.VARCHAR); //EXTRA VARCHAR
            }
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOfDucks = 0;
                if (result.next()) {
                    totalNumberOfDucks = result.getInt("count");
                }
                return totalNumberOfDucks;
            }
        }
    }


    private List<User> loadDucksOnPage(Pageable pageable, DuckFilterDTO filter) throws SQLException {
        List<User> ducksOnPage = new ArrayList<>();
        // Using StringBuilder rather than "+" operator for concatenating Strings is more performant
        // since Strings are immutable, so every operation applied on a String will create a new String
        String sql_command ="""
                            SELECT
                                u.id AS uid,
                                u.username,
                                u.email,
                                u.password,
                                d.type AS dtype,
                                d.speed,
                                d.stamina
                            FROM users u
                            JOIN ducks d ON u.id = d.id
                            """;
        Pair<String, List<Object>> sqlFilter = toSql(filter);
        if (!sqlFilter.getFirst().isEmpty()) {
            sql_command += " where " + sqlFilter.getFirst();
        }
        sql_command += " limit ? offset ?";
        try (PreparedStatement statement = connection.prepareStatement(sql_command)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getSecond()) {
                statement.setObject(++paramIndex, param, Types.VARCHAR);
            }
            statement.setInt(++paramIndex, pageable.getPageSize());
            statement.setInt(++paramIndex, pageable.getPageSize() * pageable.getPageNumber());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("uid");

                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");

                    String duckTypeStr = resultSet.getString("dtype");
                    double speed = resultSet.getDouble("speed");
                    double stamina = resultSet.getDouble("stamina");

                    DuckType duckType = DuckType.valueOf(duckTypeStr);
                    Duck duck;

                    switch (duckType) {
                        case SWIMMING ->
                                duck = new SwimmingDuck(username, email, password, duckType, speed, stamina);
                        case FLYING ->
                                duck = new FlyingDuck(username, email, password, duckType, speed, stamina);
                        case FLYING_AND_SWIMMING ->
                                duck = new FlyingSwimmingDuck(username, email, password, duckType, speed, stamina);
                        default ->
                                throw new IllegalArgumentException("Duck type invalid: " + duckTypeStr);
                    }

                    duck.setId(id);
                    ducksOnPage.add(duck);
                }
            }
        }
        return ducksOnPage;
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable, DuckFilterDTO filter) {
        try {
            int totalNumberOfDucks = duckCount(filter);
            List<User> ducksOnPage;
            if (totalNumberOfDucks > 0) {
                ducksOnPage = loadDucksOnPage(pageable, filter);
            } else {
                ducksOnPage = new ArrayList<>();
            }
            return new Page<>(ducksOnPage, totalNumberOfDucks);
        } catch (SQLException e) {
            System.out.println("SQLException caught: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable) { //cand nu am nevoie de filtru (PT DUCKS!)
        return findAllOnPage(pageable, (DuckFilterDTO) null);
    }


    /// ------------------------------------PT PAGEABLE PERSONS-----------------------------------------------
    private int personCount(PersonFilterDTO filter) throws SQLException {
        String sql_command = "SELECT count(*) AS count FROM users u JOIN persons p ON u.id = p.id";

        try (PreparedStatement statement = connection.prepareStatement(sql_command)) {
            int paramIndex = 0;
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOfPersons = 0;
                if (result.next()) {
                    totalNumberOfPersons = result.getInt("count");
                }
                return totalNumberOfPersons;
            }
        }
    }

    private List<User> loadPersonssOnPage(Pageable pageable, PersonFilterDTO filter) throws SQLException {
        List<User> personsOnPage = new ArrayList<>();
        // Using StringBuilder rather than "+" operator for concatenating Strings is more performant
        // since Strings are immutable, so every operation applied on a String will create a new String
        String sql_command ="""
                            SELECT
                                u.id AS uid,
                                u.username,
                                u.email,
                                u.password,
                                p.firstName,
                                p.lastName,
                                p.birthDate,
                                p.occupation,
                                p.empathyLevel
                            FROM users u
                            JOIN persons p ON u.id = p.id
                            """;
//        Pair<String, List<Object>> sqlFilter = toSql(filter);
//        if (!sqlFilter.getFirst().isEmpty()) {
//            sql_command += " where " + sqlFilter.getFirst();
//        }
        sql_command += " limit ? offset ?";
        try (PreparedStatement statement = connection.prepareStatement(sql_command)) {
            int paramIndex = 0;
//            for (Object param : sqlFilter.getSecond()) {
//                statement.setObject(++paramIndex, param, Types.VARCHAR);
//            }
            statement.setInt(++paramIndex, pageable.getPageSize());
            statement.setInt(++paramIndex, pageable.getPageSize() * pageable.getPageNumber());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("uid");

                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");



                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    LocalDate birthDate = resultSet.getDate("birthDate").toLocalDate();
                    String occupation = resultSet.getString("occupation");
                    Integer empathyLevel = resultSet.getInt("empathyLevel");


                    Person person = new Person(username,email,password,firstName,lastName,birthDate,occupation,empathyLevel);
                    person.setId(id);

                    personsOnPage.add(person);
                }
            }
        }
        return personsOnPage;
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable, PersonFilterDTO filter) {
        try {
            int totalNumberOfPersons = personCount(filter);
            List<User> personsOnPage;
            if (totalNumberOfPersons > 0) {
                personsOnPage = loadPersonssOnPage(pageable, filter);
            } else {
                personsOnPage = new ArrayList<>();
            }
            return new Page<>(personsOnPage, totalNumberOfPersons);
        } catch (SQLException e) {
            System.out.println("SQLException caught: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Page<User> findAllPersonsOnPage(Pageable pageable) {
        return findAllOnPage(pageable, (PersonFilterDTO) null);
    }
}
